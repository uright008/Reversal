package cn.stars.reversal.module;

import cn.stars.reversal.event.EventHandler;
import cn.stars.reversal.value.Value;
import cn.stars.reversal.util.misc.ClassUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Stream;

@Getter
@Setter
public final class ModuleManager {
    public Module[] moduleList = new Module[0];
    ArrayList<Module> modulesToAdd = new ArrayList<>();
    private Map<String, Module> moduleCache = new HashMap<>();
    private Map<Class<? extends Module>, Module> classModuleCache = new HashMap<>();


    private Module lastGetModule;
    private String lastGetModuleName;

    private String getSettingName, getSettingSettingName;
    private Value getSettingSetting;

    private List<Module> enabledModules = new ArrayList<>();
    private boolean edited = true;


    /**
     * 遍历impl下所有继承Module的class并注册
     */
    public void registerModules() {
        for (Module module : ClassUtil.instantiateList(ClassUtil.resolvePackage(this.getClass().getPackage().getName() + ".impl", Module.class))) {
            moduleList = Arrays.stream(Stream.concat(Arrays.stream(moduleList), Stream.of(module))
                    .toArray(Module[]::new)).sorted(Comparator.comparing(m -> m.getModuleInfo().name())).toArray(Module[]::new);
        }
    }

    public void registerModules(Module[] modules) {
        moduleList = modules;

        for (Module module : moduleList) {
            classModuleCache.put(module.getClass(), module);
            moduleCache.put(module.getModuleInfo().name().toLowerCase(), module);
            module.onLoad();
        }
    }

    public List<Module> getEnabledModules() {
        if (edited) {
            enabledModules = new ArrayList<>();

            for (final Module module : moduleList) {
                if (module.isEnabled()) {
                    enabledModules.add(module);
                }
            }

            edited = false;
        }

        return enabledModules;
    }

    public Module getByClass(Class<? extends Module> clazz) {
        return classModuleCache.get(clazz);
    }

    public List<Module> getModulesByCategory(final Category category) {
        final List<Module> modules = new ArrayList<>();
        for (final Module module : moduleList) {
            if (module.getModuleInfo().category() == category) modules.add(module);
        }
        return modules;
    }

    /**
     * This method returns the instance of a module gotten by the name.
     *
     * @param name The wanted module instances name.
     * @return The wanted module instance.
     */

    public Module getModule(final String name) {
        Module module = moduleCache.get(name.toLowerCase());
        if (module != null) {
            return module;
        } else {
            for (final Module m : moduleList) {
                if (m.getModuleInfo().name().equalsIgnoreCase(name)) {
                    return m;
                }
            }
        }
        return null;
    }

    /**
     * Use HashMap to get modules and settings.
     * More effective than for.
     */
    public Value getSetting(final String moduleName, final String settingName) {
        Module module = moduleCache.get(moduleName.toLowerCase());
        if (module != null) {
            Value setting = module.getSetting(settingName.toLowerCase());
            if (setting != null) {
                getSettingName = moduleName;
                getSettingSettingName = settingName;
                getSettingSetting = setting;

                return setting;
            }
        }
        return null;
    }
}
