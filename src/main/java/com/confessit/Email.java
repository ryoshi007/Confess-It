package com.confessit;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Email {

    /**
     * Verification code that will be sent to user or admin email address
     */
    private int verificationCode;

    /**
     * This method will send an email to recipient for account verification and forgot-password purposes
     * @param recipientEmail email address of the recipient
     * @param typeOfEmail type of email (user, admin or forget password)
     * @throws MessagingException if there is an error while sending the email
     */
    public void sendVerificationEmail(String recipientEmail, String typeOfEmail) throws MessagingException {

        String senderEmail = "confessit209@gmail.com";
        final String password = ""; //Confessit123

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Create a session
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, password);
            }
        });

        // Create a new message
        // Check type of email
        Message message = switch (typeOfEmail) {
            // If typeOfEmail is "user"
            case "user" -> prepareMessageForUser(session, senderEmail, recipientEmail);
            // If typeOfEmail is "admin"
            case "admin" -> prepareMessageForAdmin(session, senderEmail, recipientEmail);
            // If typeOfEmail is "forgetPassword"
            default -> prepareMessageForForgetPassword(session, senderEmail, recipientEmail);
        };

        // Send an email
        assert message != null;
        Transport.send(message);
    }

    /**
     * Prepare a message that will be sent to user for user account verification purpose
     * @param session a session created in sendVerificationEmail method
     * @param senderEmail email address of the sender
     * @param recipientEmail email address of the recipient
     * @return message that contains html part and embedded image
     */
    private Message prepareMessageForUser(Session session, String senderEmail, String recipientEmail) {
        try {
            // Generate a verification code
            verificationCodeGenerator();

            // Prepare a template to write the verification code into it
            // And create a new image
            final BufferedImage image = ImageIO.read(new File("src/main/resources/com/fxml-resources/"));
            Graphics graphics = image.getGraphics();
            graphics.setFont(graphics.getFont().deriveFont(25f));
            graphics.setColor(new Color(0));
            graphics.drawString(Integer.toString(getVerificationCode()), 260, 333);
            graphics.dispose();
            ImageIO.write(image, "png", new File("src/main/resources/com/fxml-resources/"));

            // Prepare a new message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("[Confess It] Confess It User Account Verification");

            // This email has 2 parts, the BODY and embedded image
            MimeMultipart multipart = new MimeMultipart("related");

            // Html part
            BodyPart htmlMessageBody = new MimeBodyPart();
            String htmlText = "<H1>Welcome to Confess It</H1><H2>You're nearly there!</H2><H2>Please verify your Confess It User Account</H2><img src=\"cid:image\">";
            htmlMessageBody.setContent(htmlText, "text/html");
            // Add HTML part into multipart
            multipart.addBodyPart(htmlMessageBody);

            // Image part
            BodyPart imageMessageBody = new MimeBodyPart();
            imageMessageBody.setFileName("Confess It Verification Code");
            DataSource dataSource = new FileDataSource("src/main/resources/com/fxml-resources/");
            imageMessageBody.setDataHandler(new DataHandler(dataSource));
            imageMessageBody.setHeader("Content-ID", "<image>");
            //Add image part into multipart
            multipart.addBodyPart(imageMessageBody);

            // Place the contents into the message
            message.setContent(multipart);

            return message;

        } catch (Exception ex) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Prepare a message that will be sent to user for admin account verification purpose
     * @param session a session created in sendVerificationEmail method
     * @param senderEmail email address of the sender
     * @param recipientEmail email address of the recipient
     * @return message that contains html part and embedded image
     */
    private Message prepareMessageForAdmin(Session session, String senderEmail, String recipientEmail) {

        try {
            // Generate a verification code
            verificationCodeGenerator();

            // Prepare a template to write the verification code into it
            // And create a new image
            final BufferedImage image = ImageIO.read(new File("src/main/resources/com/fxml-resources/"));
            Graphics graphics = image.getGraphics();
            graphics.setFont(graphics.getFont().deriveFont(25f));
            graphics.setColor(new Color(0));
            graphics.drawString(Integer.toString(getVerificationCode()), 260, 333);
            graphics.dispose();
            ImageIO.write(image, "png", new File("src/main/resources/com/fxml-resources/"));

            // Prepare a new message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("[Confess It] Confess It Admin Account Verification");

            // This email has 2 parts, the BODY and embedded image
            MimeMultipart multipart = new MimeMultipart("related");

            // HTML part
            BodyPart htmlMessageBody = new MimeBodyPart();
            String htmlText = "<H1>Welcome to Confess It</H1><H2>You're nearly there!</H2><H2>Please verify your Confess It Admin Account</H2><img src=\"cid:image\">";
            htmlMessageBody.setContent(htmlText, "text/html");
            // Add Html part into multipart
            multipart.addBodyPart(htmlMessageBody);

            // Image part
            BodyPart imageMessageBody = new MimeBodyPart();
            imageMessageBody.setFileName("Confess It Verification Code");
            DataSource dataSource = new FileDataSource("src/main/resources/com/fxml-resources/");
            imageMessageBody.setDataHandler(new DataHandler(dataSource));
            imageMessageBody.setHeader("Content-ID", "<image>");
            //Add image part into multipart
            multipart.addBodyPart(imageMessageBody);

            // Place the contents into the message
            message.setContent(multipart);

            return message;

        } catch (Exception ex) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Prepare a message that will be sent to user for forgot-password purpose
     * @param session a session created in sendVerificationEmail method
     * @param senderEmail email address of the sender
     * @param recipientEmail email address of the recipient
     * @return message that contains html part and embedded image
     */
    private Message prepareMessageForForgetPassword(Session session, String senderEmail, String recipientEmail) {
        try {
            // Generate a verification code
            verificationCodeGenerator();

            // Prepare a template to write the verification code into it
            // And create a new image
            final BufferedImage image = ImageIO.read(new File("src/main/resources/com/fxml-resources/"));
            Graphics graphics = image.getGraphics();
            graphics.setFont(graphics.getFont().deriveFont(25f));
            graphics.setColor(new Color(0));
            graphics.drawString(Integer.toString(getVerificationCode()), 260, 333);
            graphics.dispose();
            ImageIO.write(image, "png", new File("src/main/resources/com/fxml-resources/"));

            // Prepare a new message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("[Confess It] Reset Your Confess It Account Password");

            // This email has 2 parts, the BODY and embedded image
            MimeMultipart multipart = new MimeMultipart("related");

            // Html part
            BodyPart htmlMessageBody = new MimeBodyPart();
            String htmlText = "<H1>Welcome to Confess It</H1><H2>Your Reset Account Password Verification Code Is Generated</H2><img src=\"cid:image\">";
            htmlMessageBody.setContent(htmlText, "text/html");
            // Add HTML part into multipart
            multipart.addBodyPart(htmlMessageBody);

            // Image part
            BodyPart imageMessageBody = new MimeBodyPart();
            imageMessageBody.setFileName("Confess It Verification Code");
            DataSource dataSource = new FileDataSource("src/main/resources/com/fxml-resources/");
            imageMessageBody.setDataHandler(new DataHandler(dataSource));
            imageMessageBody.setHeader("Content-ID", "<image>");
            //Add image part into multipart
            multipart.addBodyPart(imageMessageBody);

            // Place the contents into the message
            message.setContent(multipart);

            return message;

        } catch (Exception ex) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Randomly generate a verification code that will be sent to user or admin email address
     * And assign it to verification code using setVerification method
     */
    private void verificationCodeGenerator() {
        // Generate a verification code with 6 digits
        Random r = new Random();
        final int MAX = 999999;
        final int MIN = 100000;
        setVerificationCode(r.nextInt(MAX - MIN + 1) + MIN);
    }

    /**
     * Set the verification code
     * @param verificationCode verification code that sent to user or admin email address
     */
    public void setVerificationCode(int verificationCode) {
        this.verificationCode = verificationCode;
    }

    /**
     * Get the verification code
     * @return verification code that sent to user or admin email address
     */
    public int getVerificationCode() {
        return this.verificationCode;
    }
}
