package applica.framework.android.services;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bimbobruno on 28/01/16.
 */
public class ServiceLocator {

    private enum Scope {
        PROTOTYPE,
        SINGLETON
    }

    private class ServiceDescriptor<T> {
        private Class<T> interfaceType;
        private ServiceBuilder<T> builder;
        private T singletonInstance;
        private Scope scope;
    }

    private static ServiceLocator s_serviceLocator;

    public static ServiceLocator instance() {
        if (s_serviceLocator == null) {
            s_serviceLocator = new ServiceLocator();
        }

        return s_serviceLocator;
    }

    private List<ServiceDescriptor> serviceDescriptors;

    private ServiceLocator() {
        serviceDescriptors = new ArrayList<>();
    }

    public <T> T getService(Class<T> type, Object... params) {
        for (ServiceDescriptor<T> s : serviceDescriptors) {
            if (s.interfaceType.equals(type)) {
                if (s.scope == Scope.PROTOTYPE) {
                    try {
                        return s.builder.build();
                    } catch (Exception e) {
                        throw new RuntimeException(String.format("Cannot create prototype m_instance of %s", type.getName()), e);
                    }
                } else {
                    if (s.singletonInstance == null) {
                        try {
                            s.singletonInstance = s.builder.build(params);
                        } catch (Exception e) {
                            throw new RuntimeException(String.format("Cannot create singleton m_instance of %s", type.getName()), e);
                        }
                    }

                    return s.singletonInstance;
                }
            }
        }

        throw new RuntimeException(String.format("ServiceDescriptor not registered: %s", type.getName()));
    }

    public <T> void registerService(Class<T> interfaceType, ServiceBuilder<T> builder) {
        registerService(interfaceType, builder, Scope.SINGLETON);
    }

    public <T> void registerService(Class<T> interfaceType, ServiceBuilder<T> builder, Scope scope) {
        ServiceDescriptor<T> serviceDescriptor = null;
        for (ServiceDescriptor<T> s : serviceDescriptors) {
            if (s.interfaceType.equals(interfaceType)) {
                serviceDescriptor = s;
                break;
            }
        }

        if (serviceDescriptor == null) {
            serviceDescriptor = new ServiceDescriptor<>();
            serviceDescriptor.interfaceType = interfaceType;
            serviceDescriptors.add(serviceDescriptor);
        }

        serviceDescriptor.builder = builder;
        serviceDescriptor.scope = scope;
    }

}
