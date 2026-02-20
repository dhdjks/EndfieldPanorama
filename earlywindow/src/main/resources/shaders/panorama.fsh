#version 150

uniform samplerCube uTexture;

in  vec3    texCoord;
out vec4    fragColor;

void main()
{
    fragColor = texture(uTexture, texCoord);
}