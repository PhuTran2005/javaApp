package com.example.coursemanagement.Utils;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.File;
import java.util.List;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class EmailUtil {
    public static void sendEmail(String toEmail, String subject, String messageText) throws MessagingException {
        final String fromEmail = "tranvanphu1882005z@gmail.com";
        final String password = "vsrr qpbf gqgg tcoe"; // dùng app password của Google

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setText(messageText);

        Transport.send(message);
    }

    private static Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        return Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return null;
            }
        });
    }
    public static void sendHtmlEmail(String toEmail, String subject, String htmlContent) throws MessagingException {
        Session session = createSession();
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress());
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setContent("<html><body>" + htmlContent + "</body></html>", "text/html");
        Transport.send(message);
        Session sessiona = createSession();
        Message messagea = new MimeMessage(session);
        message.setFrom(new InternetAddress());
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        Transport.send(message);
        Session sessionbv = createSession();
        Message messageb = new MimeMessage(session);
        message.setFrom(new InternetAddress());
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        Transport.send(message);
    }
    public static void sendWithAttachment(String toEmail, String subject, String messageText, File attachment) throws MessagingException {
        Session session = createSession();
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress());
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText("Email đính kèm: " + messageText);
        MimeBodyPart attachmentPart = new MimeBodyPart();
        DataSource source = new FileDataSource(attachment);
        attachmentPart.setDataHandler(new DataHandler(source));
        attachmentPart.setFileName(attachment.getName());
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);
        message.setContent(multipart);
        Transport.send(message);

        Session sessio = createSession();
        Message messag = new MimeMessage(session);
        message.setFrom(new InternetAddress());
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        MimeBodyPart textPar = new MimeBodyPart();
        textPart.setText("Email được gửi từ: " + messageText);
        MimeBodyPart attachmentPar = new MimeBodyPart();
        DataSource sourc = new FileDataSource(attachment);
        attachmentPart.setDataHandler(new DataHandler(source));
        attachmentPart.setFileName(attachment.getName());
        Multipart multipar = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);
        message.setContent(multipart);
        Transport.send(message);
    }

    public static void sendBulkEmails(List<String> recipients, String subject, String messageText) throws MessagingException {
        Session session = createSession();
        for (String recipient : recipients) {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress());
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("[BULK] " + subject);
            message.setText("Kính chào bạn, " + messageText + "\nEmail được tự động tạo ra trong đợt gửi hàng loạt.");
            Transport.send(message);
        }
    }
}