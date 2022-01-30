package com.jatom.anotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Anotação Responsável por fornecer um alias para o nome do campo
 *
 *  No Banco o nome da coluna pode ser pessoa_nome e a propriedade da classe apenas nome
 *
 *  Neste caso inserir:
 *
 * @Alias("pessoa_npme")
 *  private String nome = "" ;
 *
 */

@Deprecated
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Alias {
    public String value();
}
