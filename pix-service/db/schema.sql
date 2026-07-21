-- Banco próprio do pix-service ("pix"), separado do "kafka_saldo" do core bancário —
-- cada serviço é dono do seu schema, sem join cruzando bases.

CREATE TABLE pix_transacao (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    transacao_id  UUID          NOT NULL,             -- id de correlação com o ledger-service (correlationId lá)
    conta_id      BIGINT        NOT NULL,
    valor         NUMERIC(19,4) NOT NULL,
    tipo_movimento VARCHAR(20)  NOT NULL,              -- CREDITO / DEBITO
    status        VARCHAR(20)   NOT NULL,              -- ENVIADA / CONFIRMADA / REJEITADA
    motivo        VARCHAR(255),
    criado_em     TIMESTAMP     NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP     NOT NULL DEFAULT now(),
    CONSTRAINT uq_pix_transacao_transacao_id UNIQUE (transacao_id)
);
CREATE INDEX ix_pix_transacao_conta_id ON pix_transacao(conta_id);
