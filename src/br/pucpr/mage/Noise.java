package br.pucpr.mage;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Palups on 22/06/2017.
 */
public class Noise {
    int width, height;
    int seed;
    float[][] noise;

    public Noise(int x, int y, int seed) {
        this.width = x;
        this.height = y;
        this.seed = seed;

        noise = GeneratePerlinNoise(GenerateWhiteNoise(width, height), 7);
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

    public float[][] GenerateWhiteNoise(int width, int height)
    {
        Random random = new Random(seed);
        float[][] noise = new float[width][height];

        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                noise[i][j] = (float)random.nextDouble() % 1;
            }
        }

        return noise;
    }

    /**
     * Gera uma quantidade de octaves (arrays balanceados (smooth))
     * @param baseNoise Vetor base
     * @param octave numero de octaves
     * @return Retorna array
     */

    float[][] GenerateSmoothNoise(float[][] baseNoise, int octave)
    {
        int width = baseNoise.length;
        int height = baseNoise[0].length;

        float[][] smoothNoise = new float[width][height];

        int samplePeriod = (int) Math.pow(2, octave);
        float sampleFrequency = 1.0f / samplePeriod;

        for (int i = 0; i < width; i++)
        {
            int i0 = (i / samplePeriod) * samplePeriod;
            int i1 = (i0 + samplePeriod) % width;
            float horizontal_blend = (i - i0) * sampleFrequency;

            for (int j = 0; j < height; j++)
            {
                int j0 = (j / samplePeriod) * samplePeriod;
                int j1 = (j0 + samplePeriod) % height;
                float vertical_blend = (j - j0) * sampleFrequency;

                float top = Interpolate(baseNoise[i0][j0],
                        baseNoise[i1][j0], horizontal_blend);

                float bottom = Interpolate(baseNoise[i0][j1],
                        baseNoise[i1][j1], horizontal_blend);

                smoothNoise[i][j] = Interpolate(top, bottom, vertical_blend);
            }
        }

        return smoothNoise;
    }

    float[][] GeneratePerlinNoise(float[][] baseNoise, int octaveCount)
    {
        int width = baseNoise.length;
        int height = baseNoise[0].length;

        float[][][] smoothNoise = new float[octaveCount][][];

        float persistance = 0.5f;

        for (int i = 0; i < octaveCount; i++)
        {
            smoothNoise[i] = GenerateSmoothNoise(baseNoise, i);
        }

        float[][] perlinNoise = new float[width][height];
        float amplitude = 1.0f;
        float totalAmplitude = 0.0f;

        for (int octave = octaveCount - 1; octave >= 0; octave--)
        {
            amplitude *= persistance;
            totalAmplitude += amplitude;

            for (int i = 0; i < width; i++)
                for (int j = 0; j < height; j++)
                    perlinNoise[i][j] += smoothNoise[octave][i][j] * amplitude;
        }

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                perlinNoise[i][j] /= totalAmplitude;

        return perlinNoise;
    }

    float Interpolate(float n, float m, float alpha)
    {
        return n * (1 - alpha) + alpha * m;
    }
}