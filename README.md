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

Para os exemplos abaixo utilizaremos as seguintes tabelas:

    create table if not exists pessoa(
    
	    id serial primary key,
	    nome varchar,
	    idpessoatipo int
	    FOREIGN KEY(idpessoatipo) REFERENCES pessoa_tipo(id)
    );
    create table if not exists pessoa_telefone(
    
	    id serial primary key,
	    telefone varchar,
	    idpessoa int,
	    FOREIGN KEY(idpessoa) REFERENCES pessoa(id)
    );
    create table if not exists cpf(
    
	    id serial primary key,
	    cpfnumero varchar,
	    idpessoa int,
	    FOREIGN KEY(idpessoa) REFERENCES pessoa(id)
    );
    create table if not exists rg(
    
	    id serial primary key,
	    rgnumero varchar,
	    idpessoa int,
	    FOREIGN KEY(idpessoa) REFERENCES pessoa(id)
    );
     create table if not exists pessoa_tipo(
     
	    id serial primary key,
	    descricao varchar,
    );


#### Alias (Deprecated) - *TYPE*

Usado para nomear classes que tem nome diferente do banco de dados.

#### TableName  - *TYPE*

Usado para nomear classes de acordo com os nomes das entidades, caso o nome da entidade no banco de dados seja exatamente igual ao da classe é dispensado o uso desta anotação.

    public class Pessoa{
    
	    @Id
	    private int id;
	    private String nome;
    }

ou caso o nome da  entidade seja diferente pode colocar como parametro na anotação

	@TableName("pessoa_telefone")
    public class PessoaTelefone{

	    @Id
	    private int id;
	    private String nome;
    }


#### NoEntity  - *TYPE*

Usado quando uma classe não for uma entidade do banco de dados.

	@NoEntity
    public class Documentos{
    
	    private Cpf cpf;
	    private Rg rg;
    }


#### SimpleObject  - *FIELD*

Usado quando existe uma entidade mapeada que seja um atributo simples de uma classe.

    public class Documentos{
    
	    @SimpleObject
	    private Cpf cpf;
	    
	    @SimpleObject
	    private Rg rg;
    }

#### ListObject  - *FIELD*

Usado quando existe uma entidade mapeada que seja um atributo de lista dentro de uma classe.

    public class Pessoa{
    
	    @Id
	    private int id;
	    
	    private String nome;
	    
	    @listObject
	    private List<PessoaTelefone> telefones;
    }

####  Ignore  - *FIELD*

Usado quando existe uma variavel dentro de uma classe que não seja objeto da entidade no banco.


    public class Pessoa{
    
	    @Id
	    private int id;
	    
	    private String nome;
	    
	    private int idTipoPessoa;
		
		@Ignore
		private String descricaoTipoPessoa
	    
	    @listObject
	    private List<PessoaTelefone> telefones;

		
    }

note que a chave de referencia é o idTipoPessoa, neste caso para complementar com a descrição utilizaremos a descricaoTipoPessoa, mas este atributo não pertence a entidade na tabela pessoa, sendo assim quando for salvar o atributo será ignorado.


#### Id - *FIELD*
Indica que o atributo da classe é um campo do tipo identidade a uma chave estrangeira

o mesmo tem um parâmetro  obrigatório que deve ser inserido o nome do campo primario da tabela de referencia.
Exemplo:

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
