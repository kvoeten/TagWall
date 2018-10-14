/*
 * Copyright (C) 2018 Kaz Voeten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kazvoeten.tagwall.client.rfid;

import com.diozero.devices.MFRC522;
import com.kazvoeten.tagwall.client.ServerSocket;
import com.kazvoeten.tagwall.client.SocketHandler;
import com.kazvoeten.tagwall.client.packet.PacketHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pmw.tinylog.Configurator;

/**
 *
 * @author Kaz Voeten
 */
public class RFIDReader extends Thread {

    public static MFRC522 moduleRef;

    @Override
    public void run() {
        try (MFRC522 module = new MFRC522(24, 0, 22)) { //Change values if module is attached differently.
            RFIDReader.moduleRef = module;
            
            //Disable the annoying spam log friom diozero
            Configurator.currentConfig().resetCustomLevels();
            Configurator.currentConfig().level(org.pmw.tinylog.Level.INFO).activate();
            Configurator.currentConfig().removeAllWriters();
            module.setLogReadsAndWrites(false);
            
            //Schedule scan every second.
            ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);
            threadPool.scheduleAtFixedRate(() -> {
                scan(module);
            }, 0, 1, TimeUnit.SECONDS); //Can change the delay if this is too demanding (1 sec atm)
        
        } catch (Exception ex) {
            Logger.getLogger(RFIDReader.class.getName()).log(Level.SEVERE, "Unable to initate RFID reader module.", ex);
        }
    }

    private void scan(MFRC522 module) {
        if (!module.isNewCardPresent()) {
            return;
        }

        MFRC522.UID uid = module.readCardSerial();
        if (uid == null) {
            return;
        }

        //To interpret every UID as long (can be 8 bytes, most are 4) we cast it to long.
        byte[] UIDRaw = uid.getUidBytes();
        byte[] UID = new byte[8];
        System.arraycopy(UIDRaw, 0, UID, 0, UIDRaw.length);
        Logger.getLogger(RFIDReader.class.getName()).log(Level.INFO, "A new RFID token was found: {0}", ParseUID(UID));

        ServerSocket client = SocketHandler.socket;
        if (client == null) {
            Logger.getLogger(RFIDReader.class.getName()).log(Level.SEVERE, "Can't send token info since the server is unavailable.");
            return;
        }

        client.SendPacket(PacketHandler.tokenInfo(ParseUID(UID)));
    }

    private static long ParseUID(byte[] UID) {
        return ((long) UID[7] << 56)
                | ((long) UID[6] & 0xff) << 48
                | ((long) UID[5] & 0xff) << 40
                | ((long) UID[4] & 0xff) << 32
                | ((long) UID[3] & 0xff) << 24
                | ((long) UID[2] & 0xff) << 16
                | ((long) UID[1] & 0xff) << 8
                | ((long) UID[0] & 0xff);
    }

    public static void shutDown() {
        if (RFIDReader.moduleRef != null) {
            RFIDReader.moduleRef.close();
        }
    }
}
