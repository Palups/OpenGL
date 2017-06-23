package br.pucpr.mage;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Mylla on 22/06/2017.
 * Trabalho e TDE Perlim Noise
 */
public class Noise {
    int width, height;
    int seed;
    float[][] noise;

    public Noise(int x, int y, int seed) {
        this.width = x;
        this.height = y;
        this.seed = seed;

        noise = GeneratePerlinNoise(GenerateWhiteNoise(width, height), 6);
    }

    public float[][] GetNoise() {
        return noise;
    }
    /**
     * Gera um array com valores entre 0 e 1
     * @param width Largura do noise
     * @param height Altura do noise
     * @return Retorna o array gerado
     */
    public float[][] GenerateWhiteNoise(int width, int height) {
        Random random = new Random(seed); //seed para 0

        float[][] generatedNoise = new float[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                generatedNoise[i][j] = (float) random.nextDouble() % 1;
            }
        }
        return generatedNoise;
    }

    /**
     * Gera uma quantidade de octaves (arrays balanceados (smooth))
     * @param baseNoise Vetor base
     * @param octave numero de octaves
     * @return Retorna array
     */
    public float[][] GenerateSmoothNoise(float[][] baseNoise, int octave) {
        int width = baseNoise.length;
        int height = baseNoise[0].length;

        float[][] smoothNoise = new float[width][height];
        //int samplePeriod = (int)Math.pow(2, octave);  //2 ^ k
        int samplePeriod = 1 << octave; //transição de bits
        float sampleFrequency = 1.0f / (float)samplePeriod;

        for (int i = 0; i < width; i++) {

            //Calcula os indices horizontais
            int sample_a = (i / samplePeriod) * samplePeriod;
            int sample_b = (sample_a + samplePeriod) % height;
            float horizontalBlend = (i - sample_a) * sampleFrequency;

            for (int j = 0; j < height; j++) {
                //Calcula os indices verticais
                int sample_c = (j / samplePeriod) * samplePeriod;
                int sample_d = (sample_c + samplePeriod) % height;
                float verticalBlend = (j - sample_c) * sampleFrequency;

                //Mistura os cantos superiores
                float top = Interpolate(baseNoise[sample_a][sample_c], baseNoise[sample_b][sample_c], horizontalBlend);

                //Mistura os cantos inferiores
                float bottom = Interpolate(baseNoise[sample_a][sample_d], baseNoise[sample_b][sample_d], horizontalBlend);

                //Mistura final
                smoothNoise[i][j] = Interpolate(top, bottom, verticalBlend);
            }
        }
        return smoothNoise;
    }

    private float[][] GeneratePerlinNoise(float[][] baseNoise, int octaveCount) {
        int width = baseNoise.length;
        int height = baseNoise[0].length;

        float[][][] smoothNoise = new float[octaveCount][width][height];
        float persistence = 0.5f;

        //Gera o smooth noise
        for (int i = 0; i < octaveCount; i++) {
            smoothNoise[i] = GenerateSmoothNoise(baseNoise, i);
        }

        float[][] perlinNoise = new float[width][height];
        float amplitude = 1.0f;
        float totalAmplitude = 0.0f;

        //Mistura os noise
        for (int octave = octaveCount - 1; octave >= 0; octave--) {
            amplitude *= persistence;
            totalAmplitude += amplitude;

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    perlinNoise[i][j] = smoothNoise[octave][i][j] * amplitude;
                }
            }
        }

        //Normaliza
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                perlinNoise[i][j] /= totalAmplitude;
            }
        }

        return perlinNoise;
    }

    private float Interpolate(float n, float m, float alpha) {
        return n * (1 - alpha) + alpha * m;
    }
}
