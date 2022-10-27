package com.jatom.security;

import com.jatom.exceptions.ServiceException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validToken(String tok){
        return false;
    }

    public void outro(){

        String password = "pass";
        try {

            byte[] bytes = password.getBytes();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            System.out.println(sb.toString());

            bytes = sb.toString().getBytes();
            sb = new StringBuilder();

            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) - 0x100, 16).substring(1));
            }

            System.out.println(sb.toString());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
