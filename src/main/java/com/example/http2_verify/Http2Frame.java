package com.example.http2_verify;

import lombok.Data;

@Data
public class Http2Frame {

   private int length;
   private byte type;
   private byte flags;
   private boolean r;

   private int streamId;
   private byte[] payload;


   public  byte[] decode(byte[] data) {

       this.length = (data[0] << 16) + (data[1] << 8) + data[2];
       this.type = data[3];
       this.flags = data[4];
       this.r = (data[5] & 0x80) == 0x80 ;
       if (r) {
           this.streamId = (( data[5] | 0x80 ) << 24) + (data[6] << 16) + (data[7] << 8) +  data[8];
       } else {
           this.streamId = (data[5] << 24) + (data[6] << 16) + (data[7] << 8) +  data[8];
       }
       this.payload = new byte[length];
       for (int i = 0; i < length; i++) {
           payload[i] = data[9 + i];
       }

       if (data.length > (9 + length)) {
           byte[] bytes = new byte[data.length - (9 + length)];
           for (int i = 0; i < bytes.length; i++) {
               bytes[i] = data[(9 + length + i)];
           }
           return bytes;
       }
       return null;
   }


    public  byte[] encode() {
        byte[] frame = new byte[9 + length];
        frame[0] = (byte) (length >> 16& 0xff);
        frame[1] = (byte) (length >> 8 & 0xff);
        frame[2] = (byte) (length & 0xff);
        frame[3] = type;
        frame[4] = flags;

        if (r) {
            frame[5] = (byte)(streamId >> 24 | 0x80);
        } else {
            frame[5] = (byte) (streamId >> 24);
        }

        frame[6] = (byte) (streamId >> 16);
        frame[7] = (byte) (streamId >> 8);
        frame[8] = (byte) streamId;
        for (int i = 0; i < length; i++) {
            frame[9 + i] = payload[i];
        }
        return frame;
    }

}
