import requests

def main(inputText):
    if inputText:
        API_URL = "https://api-inference.huggingface.co/models/arpanghoshal/EmoRoBERTa"
        headers = {"Authorization": "Bearer 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"}

        def query(payload):
            response = requests.post(API_URL, headers=headers, json=payload)
            return response.json()

        output = query({
            "inputs": inputText,
        })

        EmotionCount = len(output[0])

        maxScore = output[0][0]["score"]
        maxLable = output[0][0]["label"]
        for x in range(0, EmotionCount):
            # print(output[0][x]["score"])f
            if output[0][x]["score"] > maxScore:
                maxLable = output[0][x]["label"]
                maxScore = output[0][x]["score"]
        # return maxLable, maxScore
        maxScore=maxScore

        emotion=maxLable,maxScore
        return emotion


        # print(type(emotion))
    else:
        maxScore = 0
        maxLable = "Nothing"
        emotion = maxLable, maxScore
        return emotion


