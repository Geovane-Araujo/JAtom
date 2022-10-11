# JAtom Framework

##### Ferramenta de ORM

O projeto foi criado no intuito de aprendizado para reproduzir a e entender como os ORMs funcionam  
no mesmo intuito fio desenvolvido para dar mais liberdade e facilidade para manipulação entre multiplos banco de dados uma vez que é possível controlar as conexões.

### Repositório


    <repositories>  
	     <repository> <id>myMavenRepo.read</id>  
		     <url>https://mymavenrepo.com/repo/go9Ye7KC7xaSZHqFec9g/</url>  
	     </repository>
    </repositories>

    <dependency>  
	     <groupId>org.atom</groupId>  
	     <artifactId>atom-framework</artifactId>  
	     <version>3.1.16</version>  
	     <scope>compile</scope>  
    </dependency>

### Configurações iniciais


    org.jatom.connection.driver=org.postgresql.Driver  
    org.jatom.connection.url=jdbc:postgresql://localhost:5432/<db_name>  
    org.jatom.connection.password=<senha>  
    org.jatom.connection.user=<user>  
    org.jatom.connection.schema=true  
    org.jatom.connection.multi-tenant=true
    org.jatom.connection.schema-name=<nome schema>


* schema: caso deseje usar a base como schema insira a propriedade como true, neste caso o nome do banco deverá ser informado
* multi-tenant: Em bases que serão utilizado multiplos bancos
* schema-name: caso a propriedaded multi-tenant for false e o schema for true, deverá ser informado o nome do schema do banco de dados

### Diretório Database

Caso queira que seja executado scripts de sql nativas poderá ser incluida na pasta resources uma pasta com o nome "databases".

Todos os arquivos .sql que contem neste diretório serão executados assim que o projeto for iniciado ou reiniciado



### Anotações

a biblioteca possui as seguintes anotações:

* @Alias
* @Fk
* @Id
* @Ignore
* @Join
* @ListObject
* @NoEntity
* @SimpleObject
* @TableName
* @Union


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
	     descricao varchar
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
	     @Id private int id; 
	     private String nome; 
     }  


#### NoEntity  - *TYPE*

Indica que a classe não é uma entidade do banco de dados.


     @NoEntity 
     public class Documentos{        
	     private Cpf cpf;  
	     private Rg rg; 
     }  


#### SimpleObject  - *FIELD*

Indica que o objeto é uma entidade mapeada simples


     public class Documentos{        
	     @SimpleObject  
	     private Cpf cpf;        
	     @SimpleObject  
	     private Rg rg; 
     }  


#### ListObject  - *FIELD*

Indica que o atributo da classe é uma entidade mapeada que retorna uma lista.


     public class Pessoa{        
	     @Id  
	     private int id;        
	     private String nome;  
	     @listObject  
	     private List<PessoaTelefone> telefones; 
     }  


#### Ignore  - *FIELD*

Usado quando existe uma variavel dentro de uma classe que não seja objeto da entidade no banco.



     public class Pessoa{        
	     @Id  
	     private int id;       
	      
	     private String nome;  
	     
	     private int idTipoPessoa;  

	     @Ignore  
	     private String descricaoTipoPessoa;
	           
	     @listObject  
	     private List<PessoaTelefone> telefones;  
     } 



note que a chave de referencia é o idTipoPessoa, neste caso para complementar com a descrição utilizaremos a descricaoTipoPessoa, mas este atributo não pertence a entidade na tabela pessoa, sendo assim quando for salvar o atributo será ignorado.


#### Id - *FIELD*

Indica que o atributo da classe é um campo do tipo identidade


	public class Pessoa{        
		@Id  
		private int id; 
		private String nome; 
	} 

pode se ser usado atribuindo o nome do campo na tabela caso o nome da variavel seja diferente:

	public class Pessoa{        
		@Id("id_pessoa") 
		private int id; 
		private String nome; 
	} 


#### Fk - *TYPE*

Indica que o atributo da classe é um campo referenciado a uma chave estrangeira

o mesmo tem um parâmetro  obrigatório que deve ser inserido o nome do campo primario da tabela de referencia:



     public class Pessoa {  
           
	     @Id
	     private int id; 
	     private String nome; 
     } 

     public class PessoaTefone {  
           
	     @Id  
	     private int id; 
	     
	     private String nome; 

	     @Fk("id") 
	     private int idpessoa; 
     }  
	ou:

     public class Pessoa {  
           
	     @Id("id_pessoa")
	     private int id; 
	     private String nome; 
     } 

     public class PessoaTefone {  
           
	     @Id
	     private int id; 
	     
	     private String nome; 

	     @Fk("id_pessoa") 
	     private int idpessoa; 
     } 

note que o nome que está no parâmetro é o mesmo do atributo que está como id na classe Pessoa.


#### Join - *TYPE*

Anotação que faz a união de duas tabelas durante uma consulta

Temos o seguinte cenário:



    create table if not exists pessoa(  
	      id serial PRIMARY key,  
	      nome varchar(250),  
	      idade int,  
	      idTipoPessoa int  
    ); 
    
    create table if not exists tipo_pessoa(  
	    id serial primary key,  
	    descricao varchar  
    );


onde uma pessoa possui um tipo que é previamente cadastrado podendo ser "Fisica" ou "Juridica"

Podemos abordar o seguinte cenário desta forma:

     public class Pessoa{        
   	     @Id  
	     private int id;       
	      
	     private String nome;  
	     
	     @SimpleObject
	     private TipoPessoa tipoPessoa;  

	     @Ignore  
	     private String descricaoTipoPessoa;
	           
	     @listObject  
	     private List<PessoaTelefone> telefones;  
     } 
entretanto nesta abordagem sera necessário fazer uma consulta a mais na base pois deverá fazer a busca pelo tipo da pessoa que na tabela correspondente

para estes casos pode se usar a abordagem a baixo, aplicando a anotação "@Join"

     public class Pessoa{        
   	     @Id  
	     private int id;       
	      
	     private String nome;  
	     

	     private TipoPessoa idTipoPessoa;  

	     @Ignore
	     @Join(reference = TipoPessoa.class,columnName = "descricao",columnReference = "idTipoPessoa")  
	     private String descricaoTipoPessoa;
	           
	     @listObject  
	     private List<PessoaTelefone> telefones;  
     } 

a anotação Join recebe 3 parâmetros:

- reference: referencia a classe que está mapeada .
- commnName: Nome da  coluna ao qual quer trazer o valor na tabela que irá fazer a junção.
-  columnReference: atributo da classe que é usada como chave
   estrangeira .

*** Obs: esta lib encontra-se estavel somente para uso com banco postgres