#version 150

uniform sampler2D uMaskSampler;
uniform sampler2D uBackgroundSampler;

uniform mat4 uInverseViewMatrix;
uniform mat4 uInverseProjectionMatrix;

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

float rectSDF(vec2 p, vec2 b) {
    vec2 d = abs(p) - b;
    return max(d.x, d.y);
}

float aastep(float threshold, float value) {
    float afwidth = fwidth(value);
    return smoothstep(threshold - afwidth * 0.5, threshold + afwidth * 0.5, value);
}

vec3 renderBackground()
{

    /*mat4 uViewMatrix = mat4(
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, uTime * 0.01, 1.0
    );*/

    /*const float fov = radians(70.0);
    const float aspect = 1.0; // assume square resolution for simplicity
    const float near = 0.01;
    const float far = 100.0;
    float f = 1.0 / tan(fov * 0.5);

    mat4 uProjectionMatrix = mat4(
            f/aspect, 0.0, 0.0, 0.0,
            0.0, f, 0.0, 0.0,
            0.0, 0.0, (far+near)/(near-far), -1.0,
            0.0, 0.0, 2.0*far*near/(near-far), 0.0
    );*/

    // ---------------------
    // Convert texCoord to NDC
    // ---------------------
    vec2 ndc = texCoord * 2.0 - 1.0;
    ndc.y *= -1.0;

    // ---------------------
    // Ray in world space
    // ---------------------
    mat4 invProj = uInverseProjectionMatrix;
    mat4 invView = uInverseViewMatrix;

    // NDC -> view space
    vec4 viewPos = invProj * vec4(ndc, -1.0, 1.0);
    viewPos /= viewPos.w;

    // View space -> world space
    vec3 rayDir         = normalize((invView * vec4(viewPos.xyz, 0.0)).xyz);
    vec3 rayOrigin      = (invView * vec4(0.0, 0.0, 0.0, 1.0)).xyz;

    // ---------------------
    // Plane y = -4
    // ---------------------
    float planeY        = -2.0;
    float t             = (planeY - rayOrigin.y) / rayDir.y;
//    if (t <= 0.0) return vec3(0.95);

    vec3 hit            = rayOrigin + rayDir * t;


    // ---------------------
    // Grid cell
    // ---------------------
    vec2 cellId         = floor(hit.xz);
    vec3 center         = vec3(cellId.x + 0.5, planeY, cellId.y + 0.5);


    // ---------------------
    // Billboard basis
    // ---------------------
    vec3 N              = normalize(rayOrigin - center);
    vec3 worldUp        = vec3(0.0, 1.0, 0.0);

    if (abs(dot(N, worldUp)) > 0.99)
        worldUp = vec3(0.0,0.0,1.0);

    vec3 R              = normalize(cross(worldUp, N));
    vec3 U              = cross(N, R);


    vec3 local          = hit - center;
    vec2 p              = vec2(dot(local, R), dot(local, U));


    // ---------------------
    // Shape SDF
    // ---------------------
    float size          = 0.03;
    float d             = rectSDF(p, vec2(size)); // rect

    float mask          = 1.0 - aastep(0.0, d);


    // ---------------------
    // Color with fade by distance
    // ---------------------
    vec3 bgColor        = vec3(0.95);
    vec3 shapeColor     = vec3(0.65);


    float depthFade     = clamp(t * -0.0625, 0.0, 1.0);
    float heightMask    = step(t, 0.0);
    vec3 col            = mix(bgColor, shapeColor, mask * heightMask * (1.0 - depthFade));


    return col;
}

void main() {
    const vec3 white    = vec3(0.96);

    float baseGlitch1   = triangular_wave(simple_noise(texCoord.y + uTime, 532.0)) * 0.5;
    float baseGlitch2   = square_wave(simple_noise(texCoord.y + uTime * 0.005, 68.0) * 2.0);
    float noise1        = simple_noise(uTime * 0.01, 100.0);

    float strength      = step(noise1, 0.3);
    float glitch        = (baseGlitch1 + baseGlitch2) * strength * 0.002;

    vec2 glitchCoord    = texCoord + vec2(glitch, 0.0);

    float maskColor     = texture(uMaskSampler, glitchCoord).a;
    float mask          = step(0.1, maskColor);
    vec3 front          = texture(uBackgroundSampler, texCoord).rgb;
    vec3 back           = renderBackground();
    vec3 finalColor     = mix(back, front, mask);

    fragColor           = vec4(finalColor, 1.0);
}