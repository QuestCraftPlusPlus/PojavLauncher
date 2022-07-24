package net.kdt.pojavlaunch.openxr;

//import com.mojang.blaze3d.pipeline.TextureTarget;
//import com.mojang.blaze3d.systems.RenderSystem;
//import net.minecraft.client.Minecraft;
import net.kdt.pojavlaunch.rendering.XrRenderTarget;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.system.MemoryStack.*;

public class OpenXRSwapchain implements AutoCloseable {
    public final XrSwapchain handle;
    public final OpenXRInstance instance;
    public final OpenXRSession session;

    public final int width;
    public final int height;
    public final int format;

    public final int[] arrayImages;
    public final XrRenderTarget[] leftFramebuffers;
    public final XrRenderTarget[] rightFramebuffers;

    // TODO: Replace with non-mojang code
//    public TextureTarget renderTarget;

    //TODO make two swapchains path for GL4ES compat

    public OpenXRSwapchain(XrSwapchain handle, OpenXRSession session, int format, int width, int height) {
        this.handle = handle;
        this.session = session;
        this.instance = session.instance;
        this.format = format;
        this.width = width;
        this.height = height;

        try (MemoryStack stack = stackPush()) {
            IntBuffer intBuf = stackInts(0);

            instance.checkPanic(XR10.xrEnumerateSwapchainImages(handle, intBuf, null), "xrEnumerateSwapchainImages");

            int imageCount = intBuf.get(0);
            XrSwapchainImageOpenGLESKHR.Buffer swapchainImageBuffer = XrSwapchainImageOpenGLESKHR.calloc(imageCount, stack);
            for (XrSwapchainImageOpenGLESKHR image : swapchainImageBuffer) {
                image.type(KHROpenglEsEnable.XR_TYPE_SWAPCHAIN_IMAGE_OPENGL_ES_KHR);
            }

            instance.checkPanic(XR10.xrEnumerateSwapchainImages(handle, intBuf, XrSwapchainImageBaseHeader.create(swapchainImageBuffer.address(), swapchainImageBuffer.capacity())), "xrEnumerateSwapchainImages");

            this.arrayImages = new int[imageCount];
            this.leftFramebuffers = new XrRenderTarget[imageCount];
            this.rightFramebuffers = new XrRenderTarget[imageCount];

            for (int i = 0; i < imageCount; i++) {
                XrSwapchainImageOpenGLESKHR openxrImage = swapchainImageBuffer.get(i);
                arrayImages[i] = openxrImage.image();
                leftFramebuffers[i] = new XrRenderTarget(width, height, arrayImages[i], 0);
                rightFramebuffers[i] = new XrRenderTarget(width, height, arrayImages[i], 1);
            }

            // TODO: Replace with non-mojang code
//            renderTarget = new TextureTarget((int) width, (int) height, true, false);
//            renderTarget.setClearColor(239 / 255f, 50 / 255f, 61 / 255f, 255 / 255f);
        }
    }

    int acquireImage() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer intBuf = stackCallocInt(1);
            instance.checkPanic(XR10.xrAcquireSwapchainImage(
                    handle,
                    XrSwapchainImageAcquireInfo.calloc(stack).type(XR10.XR_TYPE_SWAPCHAIN_IMAGE_ACQUIRE_INFO),
                    intBuf
            ), "xrAcquireSwapchainImage");
            instance.checkPanic(XR10.xrWaitSwapchainImage(handle,
                    XrSwapchainImageWaitInfo.calloc(stack)
                            .type(XR10.XR_TYPE_SWAPCHAIN_IMAGE_WAIT_INFO)
                            .timeout(XR10.XR_INFINITE_DURATION)
            ), "xrWaitSwapchainImage");
            return intBuf.get(0);
        }
    }

    @Override
    public void close() {
        XR10.xrDestroySwapchain(handle);
        // TODO: Replace with non-mojang code
//        if (renderTarget != null) {
//            RenderSystem.recordRenderCall(() -> {
//                for (XrRenderTarget fb : rightFramebuffers) {
//                    fb.destroyBuffers();
//                }
//                for (XrRenderTarget fb : leftFramebuffers) {
//                    fb.destroyBuffers();
//                }
//                renderTarget.destroyBuffers();
//            });
//        }
    }
}
