package com.jatom.security;

import com.jatom.exceptions.ServiceException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Security {

    private String token;

    public String generateToken(String text){
        load();
        String tok = encryptedToken();
        return tok;
    }

    public String generateToken(){
        load();
        return encryptedToken();
    }

    protected String encryptedToken(){

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(this.token.getBytes());

            byte[] bytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            // return Base64.getEncoder().encodeToString((sb.toString()+"|"+Base64.getEncoder().encodeToString(identity.getBytes())).getBytes()).replace("==","");

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validToken(String tok){

        load();
        int length = token.length();
        String aux1 = token.substring(0,(length/2));
        String aux2 = token.substring((length/2),token.length());

        tok = new String(Base64.getDecoder().decode(tok));

        String[] composition = tok.split("-");

        String aux11 = new String(Base64.getDecoder().decode(composition[0]));
        String aux22 = new String(Base64.getDecoder().decode(composition[2]));

        if(aux11.equals(aux2) && aux22.equals(aux1))
            return true;

        return false;
    }

    private String composeToken(String text) {

        int length = token.length();
        String aux1 = token.substring(0,(length/2));
        String aux2 = token.substring((length/2),token.length());
        String ad = Base64.getEncoder().encodeToString((Base64.getEncoder().encodeToString(aux2.getBytes()) + "-" +  text  + "-" + Base64.getEncoder().encodeToString(aux1.getBytes())).getBytes());
        return ad;
    }


    private void load() {

        try{
            PropertiesConfiguration config = new PropertiesConfiguration();
            config.load("application.properties");

            token = config.getString("org.jatom.configuration.key-authenticate",null);

        } catch (Exception ex){

            throw new ServiceException("BAD_REQUEST","Não foi possível conectar a base de dados",ex);
        }
    }
}
