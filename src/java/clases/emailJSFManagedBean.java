/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.util.Properties;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Tonib
 */
@Named(value = "emailJSFManagedBean")
@RequestScoped
public class emailJSFManagedBean {

private String to;
private String from;
private String subject;
private String descr;
private String username;
private String password;
private String smtp;
private int port;

/**
 * Creates a new instance of emailJSFManagedBean
 */
public emailJSFManagedBean() {
    this.to = null;
    this.from = null;
    this.subject = null;
    this.descr = null;
    this.username = null;
    this.password = null;
    this.smtp = null;
    this.port = 587;
    this.descr = "Escriba algo...";
}

public void validateEmail(FacesContext context, UIComponent toValidate, Object value) {
    String message = "";
    String email = (String) value;
    if ((email == null) || (email.equals(""))) {

        ((UIInput) toValidate).setValid(false);
        message = "Email Requerido";
        context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
    } else if ((!email.contains("@")) || (!email.contains("."))) {
        ((UIInput) toValidate).setValid(false);
        message = "Email Invalido";
        context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
    }
}

public String submitEmail() {
    Properties props = null;
    Session session = null;
    MimeMessage message = null;
    Address fromAdress = null;
    Address toAdress = null;

    props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.stattls.enable", "true");
    props.put("mail.smtp.host", smtp);
    props.put("mail.smtp.port", port);

    session = Session.getInstance(props, new javax.mail.Authenticator() {
    protected PasswordAuthentication

    getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }
    });
    message = new MimeMessage(session);
    try{
        message.setContent(getDescr(), "text/plain");
        message.setSubject(getSubject());
        InternetAddress fromAddress = new InternetAddress(getFrom());
        message.setFrom(fromAddress);
        InternetAddress toAddress = new InternetAddress(getTo());
        message.setRecipient(Message.RecipientType.TO, toAdress);
        
        message.saveChanges();
        Transport transport = session.getTransport("smtp");
        transport.connect(this.smtp,this.port,this.username,this.password);
        if(!transport.isConnected()){
            return "emailFal";
        }
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
        
    }catch(MessagingException me){
        return "emailFal";
    }
    return "emailOk";
}

public String getTo() {
    return to;
}

public void setTo(String to) {
    this.to = to;
}

public String getFrom() {
    return from;
}

public void setFrom(String from) {
    this.from = from;
}

public String getSubject() {
    return subject;
}

public void setSubject(String subject) {
    this.subject = subject;
}

public String getDescr() {
    return descr;
}

public void setDescr(String descr) {
    this.descr = descr;
}

public String getUsername() {
    return username;
}

public void setUsername(String username) {
    this.username = username;
}

public String getPassword() {
    return password;
}

public void setPassword(String password) {
    this.password = password;
}

public String getSmtp() {
    return smtp;
}

public void setSmtp(String smtp) {
    this.smtp = smtp;
}

public int getPort() {
    return port;
}

public void setPort(int port) {
    this.port = port;
}

}
