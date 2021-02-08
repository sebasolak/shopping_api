package com.example.transfer.service;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

@Service
public class GsonService {

    @Bean
    public Gson gson() {
        return new Gson();
    }

    public String deserializeJSON(String urlString) {
        try {
            URL url = new URL(urlString);
            StringBuilder jsonText = new StringBuilder();
            InputStream inputStream = url.openStream();
            Scanner scanner = new Scanner(inputStream);

            while (scanner.hasNextLine()) {
                jsonText.append(scanner.nextLine());
            }

            return jsonText.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
