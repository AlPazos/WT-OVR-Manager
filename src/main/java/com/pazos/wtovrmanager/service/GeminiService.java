package com.pazos.wtovrmanager.service;

import com.google.genai.Client;
import com.google.genai.types.Blob;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import java.util.List;


public class GeminiService {
    private String apiKey;
    private  String model;
    private String prompt ="""
    You are an expert Taekwondo competition data analyst. Analyze the tournament bracket PDF and extract scheduled matches.

    EXTRACTION & VALIDATION RULES:
    1. MATCH IDENTIFICATION: Identify the match identifier (usually a number like 1, 101, M-1, etc.) associated with each bracket node. Do not rely solely on text labels; look for numbers placed consistently in the corners or boxes of the bracket nodes.
    2. ATHLETE MAPPING: 
       - BlueAthlete: The competitor in the top or left branch.
       - RedAthlete: The competitor in the bottom or right branch.
       - Clean strings: Extract the name only. Remove club names, IDs, or bracket annotations like '(cs 1)'.
    3. FUTURE ROUNDS: For matches defined as 'Winner of Match X', set athlete objects to null.
    4. CATEGORY MAPPING (MANDATORY): Map the category found in the PDF header to the following categoryId:
       - "HOME SUB-21 P1": 1, "P2": 2, "P3": 3, "P4": 4, "P5": 5, "P6": 6, "P10": 7
       - "MULLER SUB-21 P1": 8, "P2": 9, "P3": 10, "P4": 11, "P5": 12, "P6": 13, "P8": 14, "P10": 15
    5. DATA INTEGRITY:
       -'rank' or 'seed' set to null.
       - Match numbers must be strings following the next patern 101 102 where 1 is the mat number and 01 / 02 is the match number on this mat.
       - If the bracket is complex, trace the lines to ensure the 'nextMatchNumber' reference is correct.

    OUTPUT FORMAT:
    Return a pure JSON array. Do not include markdown formatting like ```json.
    [
      {
        "matchNumber": "STRING",
        "mat": INTEGER,
        "phase": "STRING",
        "categoryId": INTEGER,
        "blueAthlete": { 
          "scoreboardName": "STRING", 
          "givenName": "STRING", 
          "familyName": "STRING", 
          "gender": "STRING", 
          "rank": INTEGER, 
          "seed": INTEGER 
        },
        "redAthlete": { 
          "scoreboardName": "STRING", 
          "givenName": "STRING", 
          "familyName": "STRING", 
          "gender": "STRING", 
          "rank": INTEGER, 
          "seed": INTEGER 
        },
        "nextMatchNumber": "STRING",
        "nextMatchColor": "STRING"
      }
    ]
    """;

    public GeminiService(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
    }

    public String extractMAtches(byte[] pdfBytes) throws Exception{
        try(Client client = Client.builder().apiKey(apiKey).build()) {
            GenerateContentConfig config = GenerateContentConfig.builder()
                    .responseMimeType("application/json")
                    .temperature(0.0f)
                    .systemInstruction(Content.builder().parts(List.of(
                            Part.builder().text(prompt).build()))
                            .build()
                    )
                    .build();
            Part pdfPart = Part.builder()
                    .inlineData(Blob.builder()
                            .data(pdfBytes)
                            .mimeType("application/pdf")
                            .build())
                    .build();
            Content content = Content.builder().parts(List.of(pdfPart)).build();

            GenerateContentResponse response = client.models.generateContent(model, content, config);
            return response.text();
        }
    }


    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
