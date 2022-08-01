package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.common.ServerActions;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SorterPacket
{
    private final byte screenID;
    private final byte button;
    private final boolean shiftPressed;
    private final BlockPos pos;

    public SorterPacket(byte screenID, byte button, boolean shiftPressed, BlockPos pos)
    {
        this.screenID = screenID;
        this.button = button;
        this.shiftPressed = shiftPressed;
        this.pos = pos;
    }

    public static SorterPacket decode(final PacketBuffer buffer)
    {
        final byte screenID = buffer.readByte();
        final byte button = buffer.readByte();
        final boolean shiftPressed = buffer.readBoolean();

        BlockPos pos = null;

        if(buffer.writerIndex() == 12)
        {
            pos = buffer.readBlockPos();
        }

        return new SorterPacket(screenID, button, shiftPressed, pos);
    }

    public static void encode(final SorterPacket message, final PacketBuffer buffer)
    {
        buffer.writeByte(message.screenID);
        buffer.writeByte(message.button);
        buffer.writeBoolean(message.shiftPressed);

        if(message.pos != null)
        {
            buffer.writeBlockPos(message.pos);
        }
    }

    public static void handle(final SorterPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity serverPlayerEntity = ctx.get().getSender();

            if(serverPlayerEntity != null)
            {
                ServerActions.sortBackpack(serverPlayerEntity, message.screenID, message.button, message.shiftPressed, message.pos);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}