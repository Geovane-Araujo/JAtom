# JAtom Framework

##### Ferramenta da ORM baseada no modelo Hibernate

O projeto foi criado no intuito de aprendizado para reproduzir a e entender como os ORMs funcionam
no mesmo intuito fio desenvolvido para dar mais liberdade e facilidade para manipulação entre multiplos banco de dados uma vez que é possível controlar as conexões.

Outro diferencial, é que o mapeamento é feito do banco para o objeto e não o contrario como se faz no hibernate, com isto é capaz de gerar todas as classes necessárias com base nas tabelas já criadas, facilitanto também na migração de sistemas.

### Anotações

a biblioteca possui as seguintes anotações:

    @Alias
    @Fk
    @Id
    @Ignore
    @Join
    @ListObject
    @NoEntity
    @SimpleObject
    @TableName
    @Union

#### Alias (Deprecated) - *FIELD*

Usado para nomear classes que tem nome diferente do banco de dados.

#### TableName  - *FIELD*

Usado para nomear classes que tem nome diferente do banco de dados.

#### NoEntity  - *FIELD*

Usado quando uma classe não for uma entidade do banco de dados.

#### SimpleObject  - *FIELD*

Usado quando existe um

#### Fk - *TYPE*
Indica que o atributo da classe é um campo referenciado a uma chave estrangeira

o mesmo tem um parâmetro  obrigatório que deve ser inserido o nome do campo primario da tabela de referencia.
Exemplo:

Tabelas:

    create table if not exists pessoa(
	    id serial primary key,
	    nome varchar
    );
    create table if not exists pessoa_telefone(
	    id serial primary key,
	    telefone varchar,
	    idpessoa int,
	    FOREIGN KEY(idpessoa) REFERENCES pessoa(id)
    );

Classes:

    public class Pessoa{
    
	    @Id
	    private int id;
	    private String nome;
    }
    public class PessoaTefone{
    
	    @Id
	    private int id;
	    private String nome;
	    @Fk("id")
	    private int idpessoa;
    }

note que o nome que está no parâmetro é o mesmo do atributo que está como id na classe Pessoa.

#### Fk - *TYPE*
Indica que o atributo da classe é um campo referenciado a uma chave estrangeira

o mesmo tem um parâmetro  obrigatório que deve ser inserido o nome do campo primario da tabela de referencia.
Exemplo:

Tabelas:

    create table if not exists pessoa(
	    id serial primary key,
	    nome varchar
    );
    create table if not exists pessoa_telefone(
	    id serial primary key,
	    telefone varchar,
	    idpessoa int,
	    FOREIGN KEY(idpessoa) REFERENCES pessoa(id)
    );

Classes:

    public class Pessoa{
    
	    @Id
	    private int id;
	    private String nome;
    }
    public class PessoaTefone{
    
	    @Id
	    private int id;
	    private String nome;
	    @Fk("id")
	    private int idpessoa;
    }

note que o nome que está no parâmetro é o mesmo do atributo que está como id na classe Pessoa.
