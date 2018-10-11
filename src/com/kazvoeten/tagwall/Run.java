/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kazvoeten.tagwall;

import com.diozero.devices.MFRC522;
import com.diozero.util.Hex;
import com.diozero.util.SleepUtil;
import java.util.Scanner;

/**
 *
 * @author Kaz Voeten
 */
public class Run {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.println("Define the controller (default 25): ");
        int controller = scan.nextInt();

        System.out.println("Define chip select (default 0): ");
        int chipsel = scan.nextInt();

        System.out.println("Define reset pin (default 22): ");
        int reset = scan.nextInt();

        try (MFRC522 mfrc522 = new MFRC522(controller, chipsel, reset)) {
            MFRC522.UID uid = null;
            while (uid == null) {
                System.out.println("Waiting for a card");
                uid = getID(mfrc522);
                System.out.println(String.format("uid: {%s}", uid));
                SleepUtil.sleepSeconds(1);
            }
        }
    }

    private static MFRC522.UID getID(MFRC522 mfrc522) {
        if (!mfrc522.isNewCardPresent()) {
            return null;
        }
        System.out.println("A card is present!");
        MFRC522.UID uid = mfrc522.readCardSerial();
        if (uid == null) {
            return null;
        }
        System.out.println(String.format("Scanned PICC's UID: {%s}", Hex.encodeHexString(uid.getUidBytes())));

        mfrc522.haltA();

        return uid;
    }

}
