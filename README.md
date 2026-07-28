# ÁguaBenta — Sistema de Gestão de Abastecimento de Água

Aplicação **desktop** para a gestão de uma empresa de abastecimento de água:
clientes, leituras de consumo, tarifas, facturação e pagamentos, com um
**servidor de notificações em tempo real** que avisa os operadores de eventos do
sistema.

> Projecto académico desenvolvido em Java na Universidade Pedagógica de Maputo
> (UP-Maputo).

---

## Funcionalidades

- Gestão de **clientes** (registo, edição, consulta)
- Registo de **leituras** de consumo
- Definição e gestão de **tarifas**
- **Facturação** e registo de **pagamentos**
- Painel de **administração** com visão geral do sistema
- **Notificações em tempo real** entre servidor e clientes da aplicação

---

## Destaques técnicos

Este projecto vai além de um CRUD comum e demonstra vários conceitos avançados:

- **Interface gráfica com Java Swing** — múltiplos painéis e componentes
  reutilizáveis.
- **Padrão DAO (Data Access Object)** — separação limpa entre a lógica de negócio
  e o acesso aos dados.
- **JDBC com `PreparedStatement`** — comunicação com MySQL protegida contra
  injeção de SQL.
- **Servidor de notificações concorrente** — comunicação por **TCP e UDP**, com
  uma _thread pool_ (`ExecutorService`) para atender vários clientes em
  simultâneo, e um receptor UDP que apresenta notificações na aplicação.
- **Credenciais externas** — a ligação à base de dados é lida de um ficheiro de
  configuração, mantendo segredos fora do código.

---

## Arquitectura

```
aguabenta/
├── model/      # Entidades do domínio (Cliente, Leitura, Pagamento, Tarifa...)
├── dao/        # Acesso a dados (DAO + ConexaoBD)
├── view/       # Interface gráfica (Swing)
├── controller/ # Coordenação entre a interface e os dados
├── server/     # Servidor de notificações (TCP/UDP + thread pool)
├── util/       # Utilitários (validação, estilos, receptor UDP)
├── Conector/   # Local para o JAR do MySQL Connector (ver "Como executar")
└── App.java    # Ponto de entrada da aplicação
```

---

## Tecnologias

- **Java** — linguagem principal
- **Java Swing** — interface gráfica
- **JDBC** — acesso à base de dados
- **MySQL** — persistência de dados
- **Sockets TCP/UDP + threads** — servidor de notificações

---

## Como executar

**Requisitos:** JDK 8+ e MySQL a correr localmente.

1. Clonar o repositório:
   ```bash
   git clone https://github.com/<o-teu-utilizador>/aguabenta.git
   cd aguabenta
   ```

2. Descarregar o **MySQL Connector/J** (ex.: `mysql-connector-j-8.0.31.jar`) e
   colocá-lo na pasta `Conector/`. *(O JAR não é versionado — ver `.gitignore`.)*

3. Configurar as credenciais da base de dados:
   ```bash
   cp db.properties.example db.properties
   ```
   Depois editar `db.properties` com o URL, utilizador e palavra-passe locais.

4. Criar a base de dados e as tabelas a partir do esquema fornecido:
   ```bash
   mysql -u root -p < schema.sql
   ```

5. Compilar e executar (a partir da pasta `aguabenta/`):
   ```bash
   javac -cp ".:Conector/mysql-connector-j-8.0.31.jar" $(find . -name "*.java")
   java  -cp ".:Conector/mysql-connector-j-8.0.31.jar" App
   ```
   > No Windows, substituir os `:` por `;` no _classpath_.

> **Segurança:** o ficheiro `db.properties` está no `.gitignore` e nunca é
> enviado para o repositório. As credenciais ficam apenas na tua máquina.

---

## Autor

Agostinho Matsule Jr.
