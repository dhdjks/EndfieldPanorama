#version 150

uniform sampler2D uMaskSampler;
uniform sampler2D uBackgroundSampler;

uniform float uTime;

in vec2 texCoord;
out vec4 fragColor;

float random_value(float v) {
    return fract(sin(v) * 43758.5453123);
}

float noise_interpolate(float a, float b, float t) {
    return (1.0-t)*a + (t*b);
}

float linear_noise(float v) {
    float i = floor(v);
    float f = fract(v);
    f = f * f * (3.0 - 2.0 * f);
    float p = abs(fract(v) - 0.5);

    float r0 = random_value(i);
    float r1 = random_value(i + 1.0);

    return noise_interpolate(r0, r1, f);
}

float simple_noise(float v, float scale) {
    float t0 = linear_noise(v * scale) * 0.125;
    float t1 = linear_noise(v * scale * 0.5) * 0.25;
    float t2 = linear_noise(v * scale * 0.25) * 0.5;

    return t0 + t1 + t2;
}

float triangular_wave(float v) {
    return 2.0 * abs( 2.0 * (v - floor(0.5 + v)) ) - 1.0;
}

float square_wave(float v) {
    return 1.0 - 2.0 * round(fract(v));
}

void main() {
    const vec3 white    = vec3(0.96);

    float baseGlitch1   = triangular_wave(simple_noise(texCoord.y + uTime, 532.0)) * 0.5;
    float baseGlitch2   = square_wave(simple_noise(texCoord.y + uTime * 0.005, 68.0) * 2.0);
    float noise1        = simple_noise(uTime * 0.01, 100.0);

    float strength      = step(noise1, 0.3);
    float glitch        = (baseGlitch1 + baseGlitch2) * strength * 0.002;

    vec2 glitchCoord    = texCoord + vec2(glitch, 0.0);

    float mask          = texture(uMaskSampler, glitchCoord).a;
    vec3 color          = texture(uBackgroundSampler, texCoord).rgb;
    vec3 finalColor     = mix(white, color, step(0.1, mask));

    fragColor           = vec4(finalColor, step(0.1, mask));
}