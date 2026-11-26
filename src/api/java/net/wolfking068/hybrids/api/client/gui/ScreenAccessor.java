package net.wolfking068.hybrids.api.client.gui;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
@SuppressWarnings("UnusedReturnValue")
public interface ScreenAccessor {

    Font getFont();

    <T extends GuiEventListener & Renderable & NarratableEntry> T invokeAddRenderableWidget_werewolves(T button);

    void invokeRemoveWidget(GuiEventListener listener);
}
