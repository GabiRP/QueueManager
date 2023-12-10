package es.gabirp.websocket;

import com.google.gson.annotations.SerializedName;

public enum MessageType {
    @SerializedName("0")
    HEARTBEAT,
    @SerializedName("1")
    HEARTBEAT_RESPONSE,
    @SerializedName("2")
    QUEUEUPDATE,
    @SerializedName("3")
    ENDQUEUE,
    @SerializedName("4")
    ERROR,
    @SerializedName("5")
    BYE,
    @SerializedName("6")
    SERVERDISCONNECT,
}
