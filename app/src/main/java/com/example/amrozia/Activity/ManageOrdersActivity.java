package com.example.amrozia.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amrozia.R;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ManageOrdersActivity extends AppCompatActivity {

    private static final String SMTP_HOST = "us2.smtp.mailhostbox.com"; // Replace with your SMTP host
    private static final String SMTP_PORT = "587"; // Port for STARTTLS, or "465" for SSL/TLS
    private static final String SMTP_USER = "support@globalfashion.in"; // Replace with your email
    private static final String SMTP_PASSWORD = "Amrozia@786"; // Replace with your email password

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_orders);

        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail("thedigitalinfinite@gmail.com", "Subject", "This is a test email for amrozia application.");
            }
        });
    }

    private void sendEmail(String recipient, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Use "true" for STARTTLS or configure SSL/TLS accordingly

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USER, SMTP_PASSWORD);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USER));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            Toast.makeText(ManageOrdersActivity.this, "Email sent successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ManageOrdersActivity.this, "Failed to send email.", Toast.LENGTH_SHORT).show();
        }
    }
}