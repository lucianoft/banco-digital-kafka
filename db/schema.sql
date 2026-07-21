-- PostgreSQL. Para Oracle (12c+): mesma sintaxe GENERATED ALWAYS AS IDENTITY funciona;
-- em versões antigas, usar CREATE SEQUENCE + trigger BEFORE INSERT. TIMESTAMP igual nos dois.
--
-- cliente/conta não moram mais aqui: vivem no conta-service (banco "conta"). As colunas
-- conta_id abaixo são só identificadores externos, sem FK — bancos separados não têm
-- como referenciar linha de outro banco.

CREATE TABLE saldo (
    conta_id      BIGINT PRIMARY KEY,
    valor         NUMERIC(19,4) NOT NULL DEFAULT 0,
    versao        BIGINT        NOT NULL DEFAULT 0,   -- coluna de lock otimista (@Version)
    atualizado_em TIMESTAMP     NOT NULL DEFAULT now()
);

-- Ledger só de movimentos efetivados: se a linha existe, o saldo já foi alterado.
-- Rejeição (saldo insuficiente, conta inválida) não gera linha aqui, só o evento
-- em transacoes-processadas.
CREATE TABLE transacao (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    conta_id         BIGINT NOT NULL,
    correlation_id   VARCHAR(100) NOT NULL,             -- id compartilhado com quem publicou (garante idempotência)
    valor            NUMERIC(19,4) NOT NULL,
    tipo_movimento   VARCHAR(20)   NOT NULL,             -- CREDITO / DEBITO
    tipo_transacao   VARCHAR(20)   NOT NULL,             -- DINHEIRO / PIX / TED
    criado_em        TIMESTAMP     NOT NULL DEFAULT now(),
    CONSTRAINT uq_transacao_correlation_id UNIQUE (correlation_id)
);
CREATE INDEX ix_transacao_conta_id ON transacao(conta_id);

-- Saldo final de cada dia por conta. Não é um fechamento em batch: toda transação
-- efetivada sobrescreve a linha do dia corrente com o valor mais recente do saldo, na
-- mesma transação de banco — quando o dia vira, a última escrita já é o valor final
-- daquele dia, sem precisar de job noturno.
CREATE TABLE saldo_diario (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    conta_id      BIGINT        NOT NULL,
    data          DATE          NOT NULL,
    valor         NUMERIC(19,4) NOT NULL,
    atualizado_em TIMESTAMP     NOT NULL DEFAULT now(),
    CONSTRAINT uq_saldo_diario_conta_data UNIQUE (conta_id, data)
);
CREATE INDEX ix_saldo_diario_conta_id ON saldo_diario(conta_id);

-- Dados de exemplo para testar o fluxo via docker-compose — conta_id = 1 é a mesma
-- conta seedada no conta-service (db/schema.sql de lá).
INSERT INTO saldo (conta_id, valor) VALUES (1, 1000.00);
