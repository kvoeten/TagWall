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
package com.kazvoeten.tagwall.server.packet;

import com.kazvoeten.tagwall.server.ClientSocket;
import data.DataStorage;
import net.OutPacket;

/**
 *
 * @author Kaz Voeten
 */
public class PacketHandler {

    public static OutPacket Verify(ClientSocket socket) {
        socket.verified = true;

        OutPacket oPacket = new OutPacket(LoopBackPacket.VERIFICATION_RESPONSE);
        DataStorage.getStorage().lock();
        try {
            oPacket.EncodeInt(DataStorage.getStorage().getQuotes().size());
            DataStorage.getStorage().getQuotes().forEach((quote) -> {
                oPacket.EncodeString(quote);
            });
        } finally {
            DataStorage.getStorage().unlock();
        }

        return oPacket;
    }
}
