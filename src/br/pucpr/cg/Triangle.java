package br.pucpr.cg;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import br.pucpr.mage.Keyboard;
import br.pucpr.mage.Scene;
import br.pucpr.mage.Window;
/**
 * Created by Palups on 10/04/2017.
 */
public class Triangle implements Scene {

	private static final String VERTEX_SHADER = "#version 330\n" + "in vec2 aPosition;\n" + "void main(){\n" +
			"	gl_Position = vec4(aPosition, 0.0, 1.0);\n" + "}";

	private static final String FRAGMENT_SHADER	= "#version 330\n" + "out vec4 out_color;\n" + "void main(){\n" +
			"out_color = vec4(1.0, 1.0, 0.0, 1.0);\n" + "}";

    private Keyboard keys = Keyboard.getInstance();

    private int vao;
    private int positions;
	private int shader;

    private int compileShader(int shaderType, String code){
    	//Compila o shader
		int shader = glCreateShader(shaderType);
		glShaderSource(shader, code);
		glCompileShader(shader);

		//Testa pela existencia de erros
		if(glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE){
			throw new RuntimeException("Unable to compile shader." + glGetProgramInfoLog(shader));
		}

		//Retorna o shader
		return shader;
	}

	public int linkProgram(int... shaders){
    	//Cria o program e associa os shaders
		int program = glCreateProgram();
		for(int shader : shaders){
			glAttachShader(program, shader);
		}

		//Far o linking e testa por erros
		glLinkProgram(program);
		if(glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE){
			throw new RuntimeException("Unable to link shaders." + glGetProgramInfoLog(program));
		}

		//Desassocia e exclui os shaders
		for(int shader:shaders){
			glDetachShader(program, shader);
			glDeleteShader(shader);
		}

		//Retorna o programa gerado
		return program;
	}

	@Override
	public void init() {		
		//Define a cor de limpeza da tela
		glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		float[] vertexData = new float[]{
				 0.0f,  0.5f,
				-0.5f, -0.5f,
				 0.5f, -0.5f
		};

		FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(vertexData.length);
		positionBuffer.put(vertexData).flip();

		positions = glGenBuffers(); //criar buffer na placa de video, em algum lugar, e passar o endereço do mesmo
		glBindBuffer(GL_ARRAY_BUFFER, positions);
		glBufferData(GL_ARRAY_BUFFER, positionBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		int vs = compileShader(GL_VERTEX_SHADER, VERTEX_SHADER);
		int fs = compileShader(GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
		shader = linkProgram(vs, fs);
	}

	@Override
	public void update(float secs) {	
        if (keys.isPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(glfwGetCurrentContext(), GLFW_TRUE);
            return;
        }
	}

	@Override
	public void draw() {
		glClear(GL_COLOR_BUFFER_BIT);

		glUseProgram(shader);
		glBindVertexArray(vao);

		//Associa o buffer "positions" ao atributo "aPosition"
		int aPosition = glGetAttribLocation(shader, "aPosition");
		glEnableVertexAttribArray(aPosition);
		glBindBuffer(GL_ARRAY_BUFFER, positions);
		glVertexAttribPointer(aPosition, 2, GL_FLOAT, false, 0, 0);

		//Comanda o desenho
		glDrawArrays(GL_TRIANGLES, 0, 3);

		//Faxina
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(aPosition);
		glBindVertexArray(0);
		glUseProgram(0);
	}

	@Override
	public void deinit() {
	}

	public static void main(String[] args) {
		new Window(new Triangle()).show();
	}
}
