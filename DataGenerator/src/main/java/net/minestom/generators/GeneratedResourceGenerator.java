package net.minestom.generators;

import com.google.gson.JsonObject;
import net.minestom.datagen.DataGenerator;
import org.jetbrains.annotations.NotNull;

public final class GeneratedResourceGenerator extends DataGenerator {
    private final String name;

    public GeneratedResourceGenerator(@NotNull String name) {
        this.name = name;
    }

    @Override
    public JsonObject generate() {
        var result = mergePath(DATA_FOLDER.resolve(name));
        if (result.isEmpty()) {
            throw new IllegalStateException("Generated resource directory is empty: " + name);
        }
        return result;
    }
}
