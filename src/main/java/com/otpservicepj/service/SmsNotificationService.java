package com.otpservicepj.service;

import org.jsmpp.bean.*;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class SmsNotificationService implements NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(SmsNotificationService.class);

    private final String host;
    private final int port;
    private final String systemId;
    private final String password;
    private final String systemType;
    private final String sourceAddress;

    public SmsNotificationService() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("sms.properties")) {
            if (input == null) {
                logger.error("Sorry, unable to find sms.properties");
                throw new IOException("sms.properties not found");
            }
            props.load(input);
        } catch (IOException e) {
            logger.error("Failed to load sms.properties", e);
        }
        this.host = props.getProperty("smpp.host");
        this.port = Integer.parseInt(props.getProperty("smpp.port"));
        this.systemId = props.getProperty("smpp.system_id");
        this.password = props.getProperty("smpp.password");
        this.systemType = props.getProperty("smpp.system_type");
        this.sourceAddress = props.getProperty("smpp.source_addr");
    }

    @Override
    public void send(String destination, String code) {
        SMPPSession session = new SMPPSession();
        try {
            BindParameter bindParam = new BindParameter(BindType.BIND_TX, systemId, password, systemType,
                TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, sourceAddress);
            session.connectAndBind(host, port, bindParam);

            session.submitShortMessage(
                systemType, TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, sourceAddress,
                TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, destination,
                new ESMClass(), (byte) 0, (byte) 1, null, null,
                new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT), (byte) 0,
                new GeneralDataCoding(Alphabet.ALPHA_DEFAULT), (byte) 0,
                ("Your code: " + code).getBytes(StandardCharsets.UTF_8)
            );
            logger.info("SMS sent to {}", destination);
        } catch (Exception e) {
            logger.error("Failed to send SMS", e);
        } finally {
            session.unbindAndClose();
        }
    }
}
