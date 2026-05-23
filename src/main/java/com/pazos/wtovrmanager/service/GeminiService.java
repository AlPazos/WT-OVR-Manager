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
    private String prompt = """
    You are an expert Taekwondo competition data analyst. Analyze the tournament bracket PDF and extract scheduled matches.

    EXTRACTION & VALIDATION RULES:
    1. MATCH IDENTIFICATION: Identify the match identifier (usually a number like 1, 101, M-1, etc.) associated with each bracket node. Do not rely solely on text labels; look for numbers placed consistently in the corners or boxes of the bracket nodes.
    2. ATHLETE MAPPING & ID GENERATION: 
       - Generate IDs: Assign a unique sequential number as a string (e.g., "1", "2", "3") to the 'ovrInternalId' field for each new athlete as you encounter them in the bracket. If the same athlete appears in multiple matches (e.g., advancing to the next round), reuse their previously assigned ID.
       - BlueAthlete: The competitor in the top or left branch.
       - RedAthlete: The competitor in the bottom or right branch.
       - Clean strings: Extract the name only. Remove club names, IDs, or bracket annotations like '(cs 1)'.
    3. PHASE NAMING: Identify the tournament round for each match based on the bracket structure and populate the 'phase' field using ONLY standard abbreviations: 'F' (Final), 'SF' (Semi-Finals), 'QF' (Quarter-Finals), 'R16' (Round of 16), 'R32' (Round of 32), 'R64' (Round of 64), etc.
    4. FUTURE ROUNDS: For matches defined as 'Winner of Match X', set athlete objects to null.
    5. CATEGORY MAPPING (MANDATORY): Map the category found in the PDF header to the following categoryId:
       - "HOME SUB-21 P1": 1, "P2": 2, "P3": 3, "P4": 4, "P5": 5, "P6": 6, "P10": 7
       - "MULLER SUB-21 P1": 8, "P2": 9, "P3": 10, "P4": 11, "P5": 12, "P6": 13, "P8": 14, "P10": 15
    6. DATA INTEGRITY:
       - 'rank' or 'seed' set to null.
       - Match numbers must be strings following the next patern 101 102 where 1 is the mat number and 01 / 02 is the match number on this mat.
       - If the bracket is complex, trace the lines to ensure the 'nextMatchNumber' reference is correct.
    7. SORTING (CRITICAL): Before generating the final JSON, you MUST sort the entire array in ascending numerical order based on the 'matchNumber' field. For example, "101" must appear before "102".

    OUTPUT FORMAT:
    Return a pure JSON array. Do not include markdown formatting like ```json.
    [
      {
        "matchNumber": "STRING",
        "mat": INTEGER,
        "phase": "STRING",
        "categoryId": INTEGER,
        "blueAthlete": { 
          "ovrInternalId": "STRING",
          "scoreboardName": "STRING", 
          "givenName": "STRING", 
          "familyName": "STRING", 
          "gender": "STRING", 
          "rank": INTEGER, 
          "seed": INTEGER 
        },
        "redAthlete": { 
          "ovrInternalId": "STRING",
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
