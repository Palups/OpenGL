#version 330

in vec3 vNormal;
in vec3 vViewPath;
out vec4 outColor; //cor da luz ambiente

uniform vec3 uAmbientLight; //cor da luz difusa
uniform vec3 uDiffuseLight; //direção da luz difusa
uniform vec3 uSpecularLight; //Cor do material ambiente

// Propriedade da luz direcional
uniform vec3 uLightDir;

uniform vec3 uAmbientMaterial; //direção do material difusa
uniform vec3 uDiffuseMaterial; //cor da luz especular
uniform vec3 uSpecularMaterial;

//Sensibilidade do material a luz especular
uniform float uSpecularPower;


void main() {
    vec3 N = normalize(vNormal);
    vec3 L = normalize(uLightDir);

    //Calculo do componente ambiente
    vec3 ambient = uAmbientLight * uAmbientMaterial;

    //Calcula o componente difuso
    float intensity = dot(N, -L); // cosseno entre os vetores normalizados N e L
    vec3 diffuse = clamp(uDiffuseLight * intensity, 0.0, 1.0) * uDiffuseMaterial;

    //Calcula o componente Especular
    float specularIntensity = 0.0f;
    if(uSpecularPower > 0.0){
        vec3 V = normalize(vViewPath);
        vec3 R = reflect(L, N);
        specularIntensity = pow(max(dot(R, V), 0.0), uSpecularPower);
    }
    vec3 specular = specularIntensity * uSpecularLight * uSpecularMaterial;
    //Combina os componentes
    vec3 color = clamp(ambient + diffuse + specular, 0.0, 1.0);
    outColor = vec4(color, 1.0f);
}