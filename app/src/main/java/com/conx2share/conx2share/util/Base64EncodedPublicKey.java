package com.conx2share.conx2share.util;

import javax.inject.Singleton;

@Singleton
public class Base64EncodedPublicKey {

    String base64EncodedPublicKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw8wBW2+p583cF3zzGVmjb3vGq/Lm/Uy4CJJg84pYWKf03pZjlB1Gwco4Vs3oA6YrHQSwbDVN58XLoZvdIFyCHOjqzq"
                    + "MqVbT+fxeQyYEKT+YjT73qI1vcmPVSZVV04ihhFtHcDhYkWxZtNPb7um8LMxcsvGmXbmJC7Uqe8PtblGFZxXBKPoh0XDvicfeLmSThvlvEn9skPuDJweO7h18rYZ"
                    + "tdX20e9whifj0cmcMGdLQIFOuxXtg/EySVI0gM3OOwPazjufWJQEs5bWovbDz6AcUjpVmReT2t7v+wL8kafCEuIEBSXiLdW4hm/MPUHY/gi6P6PynOUJ0eizsJafdWWQIDAQAB";

    public String getBase64EncodedPublicKey() {
        return base64EncodedPublicKey;
    }
}
