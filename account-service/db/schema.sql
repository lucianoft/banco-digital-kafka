-- Banco próprio do account-service ("conta"), separado do "ledger" (core bancário) e
-- do "pix" — cada serviço é dono do seu schema, sem join cruzando bases.

CREATE TABLE cliente (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome        VARCHAR(150) NOT NULL,
    documento   VARCHAR(20)  NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'ATIVO',
    criado_em   TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT uq_cliente_documento UNIQUE (documento)
);

CREATE TABLE conta (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cliente_id    BIGINT NOT NULL REFERENCES cliente(id),
    numero_conta  VARCHAR(20)  NOT NULL,
    tipo          VARCHAR(20)  NOT NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'ATIVA',
    criado_em     TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT uq_conta_numero UNIQUE (numero_conta)
);
CREATE INDEX ix_conta_cliente_id ON conta(cliente_id);

-- Mesma conta_id=1 usada nos dados de exemplo do ledger-service (saldo, transacao).
INSERT INTO cliente (nome, documento, status) VALUES ('Maria Souza', '12345678900', 'ATIVO');
INSERT INTO conta (cliente_id, numero_conta, tipo, status) VALUES (1, '0001-1', 'CORRENTE', 'ATIVA');
