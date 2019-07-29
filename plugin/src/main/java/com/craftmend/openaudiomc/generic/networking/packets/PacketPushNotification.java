package com.craftmend.openaudiomc.generic.networking.packets;

import com.craftmend.openaudiomc.generic.networking.abstracts.AbstractPacket;
import com.craftmend.openaudiomc.generic.networking.enums.PacketChannel;
import com.craftmend.openaudiomc.generic.networking.payloads.NotificationPayload;

public class PacketPushNotification extends AbstractPacket {

    public PacketPushNotification(String message) {
        super(
                new NotificationPayload(message),
                PacketChannel.CLIENT_OUT_PUSH_NOTIFICATION,
                null
        );
    }

}
