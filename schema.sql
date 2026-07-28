-- =====================================================================
-- ÁguaBenta — Esquema da base de dados
-- Sistema de Gestão de Abastecimento de Água
-- ---------------------------------------------------------------------
-- Cria apenas a ESTRUTURA (tabelas). 

CREATE DATABASE IF NOT EXISTS aguas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE aguas;

-- ---------------------------------------------------------------------
-- Utilizadores do sistema (funcionários: Admin e Gestor)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS utilizador (
    codUtilizador  INT AUTO_INCREMENT PRIMARY KEY,
    nome           VARCHAR(150)   NOT NULL,
    email          VARCHAR(150)   NOT NULL UNIQUE,
    senha          VARCHAR(255)   NOT NULL,
    perfil         ENUM('Admin','Gestor') NOT NULL,
    dataContrato   DATE,
    dataCadastro   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Clientes (consumidores de água)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cliente (
    codCliente     INT AUTO_INCREMENT PRIMARY KEY,
    nome           VARCHAR(150)   NOT NULL,
    endereco       VARCHAR(255),
    codFunc        INT,                       -- gestor que cadastrou o cliente
    dataContrato   DATE,

    CONSTRAINT fk_cliente_func
        FOREIGN KEY (codFunc) REFERENCES utilizador(codUtilizador)
        ON DELETE SET NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Leituras de consumo
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS leitura (
    codLei          INT AUTO_INCREMENT PRIMARY KEY,
    codCli          INT            NOT NULL,
    leituraAnterior DOUBLE         NOT NULL DEFAULT 0,
    leituraActual   DOUBLE         NOT NULL DEFAULT 0,
    dataLeitura     DATE           NOT NULL,
    valorPagar      DOUBLE         NOT NULL DEFAULT 0,
    estadoPaga      BOOLEAN        NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_leitura_cliente
        FOREIGN KEY (codCli) REFERENCES cliente(codCliente)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Pagamentos (associados a uma leitura)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pagamento (
    codPag         INT AUTO_INCREMENT PRIMARY KEY,
    codLei         INT            NOT NULL,
    valorPago      DOUBLE         NOT NULL DEFAULT 0,
    dataPagamento  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metodoPaga     VARCHAR(50),

    CONSTRAINT fk_pagamento_leitura
        FOREIGN KEY (codLei) REFERENCES leitura(codLei)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Tarifas (histórico de valores por metro cúbico)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tarifa (
    codTar               INT AUTO_INCREMENT PRIMARY KEY,
    valorPorMetroCubico  DOUBLE   NOT NULL,
    taxaMinima           DOUBLE   NOT NULL DEFAULT 0,
    dataActualizacao     DATE     NOT NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Notificações geradas pelo sistema
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS notificacao (
    codNotificacao   INT AUTO_INCREMENT PRIMARY KEY,
    tipo             VARCHAR(50)  NOT NULL,   -- Pagamento, Leitura, Cliente, Tarifa, Gestor
    mensagem         VARCHAR(255) NOT NULL,
    nomeFuncionario  VARCHAR(150),
    dataHora         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;
