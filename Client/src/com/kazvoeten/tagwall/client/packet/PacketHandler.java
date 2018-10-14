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
package com.kazvoeten.tagwall.client.packet;

import com.kazvoeten.tagwall.client.ServerSocket;
import com.kazvoeten.tagwall.client.visual.DataStorage;
import java.util.ArrayList;
import net.InPacket;
import net.OutPacket;

/**
 *
 * @author Kaz Voeten
 */
public class PacketHandler {
    public static void Verify(ServerSocket socket, InPacket iPacket) {
        if (iPacket.DecodeShort() == ServerPacket.VERIFICATION_RESPONSE) {
            socket.verified = true;
            
            int size = iPacket.DecodeInt();
            ArrayList<String> quotes = new ArrayList<>();
            for(int i = 0; i < size; i++) {
                quotes.add(iPacket.DecodeString());
            }
            
            DataStorage.getStorage().SetQuotes(quotes);
        }
    }
    
    public static OutPacket verificationRequest() {
        OutPacket oPacket = new OutPacket(LoopBackPacket.VERIFICATION_REQUEST);
        return oPacket;
    }
    
    public static OutPacket tokenInfo(long uid) {
        OutPacket oPacket = new OutPacket(LoopBackPacket.TOKEN_INFO);
        oPacket.EncodeLong(uid);
        return oPacket;
    }
}
