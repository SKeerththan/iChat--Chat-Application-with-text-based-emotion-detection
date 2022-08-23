package keerththan.sk.ichat.Models;

public class MessageModel {
    String uId,message,messageId;
    Long timestamp;
    String language ;
    Float sendMessageEmotionScore;
    String engTranslatedText;

    public MessageModel(String translatedMessageReciever) {
        this.translatedMessageReciever = translatedMessageReciever;
    }

    String sendMessageEmotionLable;

    public String getTranslatedMessageReciever() {
        return translatedMessageReciever;
    }

    public void setTranslatedMessageReciever(String translatedMessageReciever) {
        this.translatedMessageReciever = translatedMessageReciever;
    }

    String translatedMessageReciever;






    public MessageModel(String uId, String message, Long timestamp ,String language) {
        this.uId = uId;
        this.message = message;
        this.timestamp = timestamp;
        this.language=language;
    }

    public MessageModel(String uId, String message,String engTranslatedText,String sendMessageEmotionLable, Float sendMessageEmotionScore) {
        this.uId = uId;
        this.message = message;
        this.engTranslatedText=engTranslatedText;
        this.sendMessageEmotionLable =sendMessageEmotionLable;
        this.sendMessageEmotionScore=sendMessageEmotionScore;
    }


    public Float getSendMessageEmotionScore() {
        return sendMessageEmotionScore;
    }

    public void setSendMessageEmotionScore(Float sendMessageEmotionScore) {
        this.sendMessageEmotionScore = sendMessageEmotionScore;
    }






    public String getSendMessageEmotionLable() {
        return sendMessageEmotionLable;
    }

    public void setSendMessageEmotionLable(String sendMessageEmotionLable) {
        this.sendMessageEmotionLable = sendMessageEmotionLable;
    }


    public MessageModel(){}

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMessage() {


        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    public String getEngTranslatedText() {
        return engTranslatedText;
    }

    public void setEngTranslatedText(String engTranslatedText) {
        this.engTranslatedText = engTranslatedText;
    }
}
