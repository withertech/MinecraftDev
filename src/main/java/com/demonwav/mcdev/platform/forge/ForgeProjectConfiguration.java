package com.demonwav.mcdev.platform.forge;

import com.demonwav.mcdev.buildsystem.BuildSystem;
import com.demonwav.mcdev.platform.PlatformType;
import com.demonwav.mcdev.platform.ProjectConfiguration;

import com.intellij.ide.util.EditorHelper;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ForgeProjectConfiguration extends ProjectConfiguration {

    public List<String> dependencies = new ArrayList<>();
    public String updateUrl;

    public String mcpVersion;
    public String forgeVersion;

    public ForgeProjectConfiguration() {
        type = PlatformType.FORGE;
    }

    public boolean hasDependencies() {
        return listContainsAtLeastOne(dependencies);
    }

    public void setDependencies(String string) {
        this.dependencies.clear();
        Collections.addAll(this.dependencies, commaSplit(string));
    }

    @Override
    public void create(@NotNull Module module, @NotNull BuildSystem buildSystem, @NotNull ProgressIndicator indicator) {
        ApplicationManager.getApplication().invokeAndWait(() -> ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                indicator.setText("Writing main class");
                VirtualFile file = buildSystem.getSourceDirectories().get(0);
                String[] files = this.mainClass.split("\\.");
                String className = files[files.length - 1];
                String packageName = this.mainClass.substring(0, this.mainClass.length() - className.length() - 1);
                for (int i = 0, len = files.length - 1; i < len; i++) {
                    String s = files[i];
                    VirtualFile temp = file.findChild(s);
                    if (temp != null && temp.isDirectory()) {
                        file = temp;
                    } else {
                        file = file.createChildDirectory(this, s);
                    }
                }

                VirtualFile mainClassFile = file.findOrCreateChildData(this, className + ".java");
                ForgeTemplate.applyMainClassTemplate(
                        module,
                        mainClassFile,
                        packageName,
                        buildSystem.getArtifactId(),
                        pluginName,
                        pluginVersion,
                        className
                );

                file = buildSystem.getResourceDirectories().get(0);
                VirtualFile mcmodInfoFile = file.findOrCreateChildData(this, "mcmod.info");

                String authorsText = "";
                if (hasAuthors()) {
                    Iterator<String> iterator = authors.iterator();
                    while (iterator.hasNext()) {
                        authorsText += '"' + iterator.next() + '"';
                        if (iterator.hasNext()) {
                            authorsText += ", ";
                        }
                    }
                }
                if (authorsText.equals("")) {
                    authorsText = null;
                }

                String dependenciesText = "";
                if (hasDependencies()) {
                    Iterator<String> iterator = dependencies.iterator();
                    while (iterator.hasNext()) {
                        dependenciesText += '"' + iterator.next() + '"';
                        if (iterator.hasNext()) {
                            dependenciesText += ", ";
                        }
                    }
                    if (dependenciesText.equals("")) {
                        dependenciesText = null;
                    }
                }

                ForgeTemplate.applyMcmodInfoTemplate(
                        module,
                        mcmodInfoFile,
                        buildSystem.getArtifactId(),
                        pluginName,
                        description,
                        website,
                        updateUrl,
                        authorsText,
                        dependenciesText
                );

                // Set the editor focus on the main class
                PsiFile mainClassPsi = PsiManager.getInstance(module.getProject()).findFile(mainClassFile);
                if (mainClassPsi != null) {
                    EditorHelper.openInEditor(mainClassPsi);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }), ModalityState.any());
    }
}
