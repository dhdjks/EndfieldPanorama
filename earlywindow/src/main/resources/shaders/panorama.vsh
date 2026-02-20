#version 150

uniform mat4 uProjectionViewMatrix;

in  vec3 Position;
out vec3 texCoord;

void main()
{
	texCoord = Position;
	gl_Position = uProjectionViewMatrix * vec4(Position, 1.0);
}