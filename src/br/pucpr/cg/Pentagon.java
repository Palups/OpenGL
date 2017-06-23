package br.pucpr.cg;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import br.pucpr.mage.Keyboard;
import br.pucpr.mage.Scene;
import br.pucpr.mage.Window;
/**
 * Created by Palups on 21/06/2017.
 */
public class Pentagon implements Scene {

    private static final String	VERTEX_SHADER = "#version 330\n" + "in	vec2 aPosition;\n" + "void main(){\n" +
            "gl_Position = vec4(aPosition, 0.0, 1.0);\n" + "}";

    private static final String	FRAGMENT_SHADER	= "#version	330\n" 	+ "out vec4 out_color;\n" + "void main(){\n" +
            "out_color = vec4(1.0, 0.5, 1.0, 1.0);\n"	+ "}";

    private Keyboard keys = Keyboard.getInstance();
    private int vao;
    private int position;
    private int shader;
    private int indices;

    private int compileShader(int shaderType, String code)
    {
        //Compila o shader
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, code);
        glCompileShader(shader);

        //Testa pela existencia de erros
        if(glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE)
        {
            throw new RuntimeException("Unable to compile shader" + glGetShaderInfoLog(shader));
        }

        //Retorna o shader
        return shader;
    }

    public void init() {
        //Define a cor de limpeza da tela
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        float [] vertexData = new float[]{
                00.0f, 00.9f,
                -0.5f, 00.45f,
                -0.3f, -0.5f,
                00.3f, -0.5f,
                00.5f, 00.45f
        };

        int indexData[] = new int[] {
                0, 1, 4,
                4, 1, 2,
                2, 3, 4
        };

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        FloatBuffer positionBuffer	= BufferUtils.createFloatBuffer(vertexData.length);
        positionBuffer.put(vertexData).flip();

        position = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, position);
        glBufferData(GL_ARRAY_BUFFER, positionBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indexData.length);
        indexBuffer.put(indexData).flip();
        indices = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        int vs = compileShader(GL_VERTEX_SHADER, VERTEX_SHADER);
        int fs = compileShader(GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
        shader = linkProgram(vs, fs);
    }

    public int linkProgram(int... shaders)
    {
        //Cria o programa e associa os shaders
        int program = glCreateProgram();
        for (int shader: shaders)
        {
            glAttachShader(program, shader);
        }

        //Faz o linking e testa por erros
        glLinkProgram(program);
        if(glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE)
        {
            throw new RuntimeException("Unable to link shaders" + glGetProgramInfoLog(program));
        }

        //Desasocia e exclui os shader
        for(int shader : shaders)
        {
            glDetachShader(program, shader);
            glDeleteShader(shader);
        }

        //Retorn o programa gerado
        return 	program;
    }

    public void update(float secs) {
        if (keys.isPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(glfwGetCurrentContext(), GLFW_TRUE);
            return;
        }
    }

    public void draw()
    {
        glClear(GL_COLOR_BUFFER_BIT);
        glUseProgram(shader);
        glBindVertexArray(vao);

        //Associa o buffer "positions" ao atributo "aPosition"
        int aPosition = glGetAttribLocation(shader, "aPosition");
        glEnableVertexAttribArray(aPosition);
        glBindBuffer(GL_ARRAY_BUFFER, position);
        glVertexAttribPointer(aPosition, 2, GL_FLOAT, false, 0, 0);

        //Indices
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indices);
        glDrawElements(GL_TRIANGLES, 9, GL_UNSIGNED_INT, 0);

        //Faxina
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(aPosition);
        glBindVertexArray(0);
        glUseProgram(0);
    }

    public void deinit() {
    }

    public static void main(String[] args) {
        new Window(new Pentagon()).show();
    }
}