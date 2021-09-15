package br.dev.juniorlatalisa.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author juniorlatalisa
 */
public final class ReflectUtils {

	private ReflectUtils() {
	}

	/**
	 * Obter a lista de fields de uma determinada classe.
	 * 
	 * @see ReflectUtils#getFields(Class, List)
	 */
	public static List<Field> getFieldList(Class<?> classe) {
		return getFields(classe, new ArrayList<Field>());
	}

	/**
	 * Obter o mapa de fields de uma determinada classe.
	 * 
	 * @see ReflectUtils#getFields(Class, Map)
	 */
	public static Map<String, Field> getFieldMap(Class<?> classe) {
		return getFields(classe, new HashMap<String, Field>());
	}

	/**
	 * Carregar os Fields da classe informada na lista de retorno. Os Fields das
	 * superclasses serão carregados recursivamente.
	 * 
	 * @see Class#getDeclaredFields()
	 * @see Class#getSuperclass()
	 */
	public static List<Field> getFields(Class<?> classe, List<Field> retorno) {
		if (!((classe == null) || (Object.class.equals(classe)))) {
			retorno.addAll(0, Arrays.asList(classe.getDeclaredFields()));
			getFields(classe.getSuperclass(), retorno);
		}
		return retorno;
	}

	/**
	 * Carregar os Fields da classe informada no mapa de retorno indexado pelo nome
	 * do Field. Os Fields das superclasses serão carregados recursivamente.
	 * 
	 * @see Class#getDeclaredFields()
	 * @see Class#getSuperclass()
	 */
	public static Map<String, Field> getFields(Class<?> classe, Map<String, Field> retorno) {
		if (!((classe == null) || (Object.class.equals(classe)))) {
			String key;
			for (Field field : classe.getDeclaredFields()) {
				key = field.getName();
				if (!retorno.containsKey(key)) {
					retorno.put(key, field);
				}
			}
			getFields(classe.getSuperclass(), retorno);
		}
		return retorno;
	}

	/**
	 * Obter os métodos de uma determinada classe.
	 * 
	 * @see ReflectUtils#getMethods(Class, List)
	 */
	public static List<Method> getMethodList(Class<?> classe) {
		return getMethods(classe, new ArrayList<Method>());
	}

	/**
	 * Obter os métodos de uma determinada classe.
	 * 
	 * @see ReflectUtils#getMethods(Class, Map)
	 */
	public static Map<String, Method> getMethodMap(Class<?> classe) {
		return getMethods(classe, new HashMap<String, Method>());
	}

	/**
	 * Obter os métodos de uma determinada classe e adicionar à lista de retorno. Os
	 * Methods das superclasses serão carregados recursivamente.
	 * 
	 * @see Class#getDeclaredMethods()
	 * @see Class#getSuperclass()
	 */
	public static List<Method> getMethods(Class<?> classe, List<Method> retorno) {
		if (!((classe == null) || (Object.class.equals(classe)))) {
			retorno.addAll(0, Arrays.asList(classe.getDeclaredMethods()));
			getMethods(classe.getSuperclass(), retorno);
		}
		return retorno;
	}

	/**
	 * Carregar os métodos da classe informada no mapa de retorno indexado pelo nome
	 * do Method. Os métodos das superclasses serão carregados recursivamente.
	 * 
	 * @see Class#getDeclaredMethods()
	 * @see Class#getSuperclass()
	 */
	public static Map<String, Method> getMethods(Class<?> classe, Map<String, Method> retorno) {
		if (!((classe == null) || (Object.class.equals(classe)))) {
			String key;
			for (Method method : classe.getDeclaredMethods()) {
				key = method.getName();
				if (!retorno.containsKey(key)) {
					retorno.put(key, method);
				}
			}
			getMethods(classe.getSuperclass(), retorno);
		}
		return retorno;
	}

	/**
	 * @see Introspector#getBeanInfo(Class)
	 * @see BeanInfo#getPropertyDescriptors()
	 * @see IntrospectionException
	 */
	public static PropertyDescriptor getPropertyDescriptor(Field field) {
		BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(field.getDeclaringClass());
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
		PropertyDescriptor[] propDescList = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor pd : propDescList) {
			if (pd.getName().equals(field.getName())) {
				return pd;
			}
		}
		return null;
	}

	/**
	 * Obter o método de configuração (set).
	 * 
	 */
	public static Method getWriteMethod(Field field) {
		return getPropertyDescriptor(field).getWriteMethod();
	}

	/**
	 * Obter o método de acesso (get).
	 * 
	 */
	public static Method getReadMethod(Field field) {
		return getPropertyDescriptor(field).getReadMethod();
	}

	/**
	 * Alterar o valor do field de um determinado objeto através do seu método set.
	 * 
	 * @see ReflectUtils#getWriteMethod(Field)
	 * @see IllegalAccessException
	 * @see IllegalArgumentException
	 * @see InvocationTargetException
	 */
	public static Object setFieldValue(Object obj, Field field, Object value) {
		try {
			return getWriteMethod(field).invoke(obj, value);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Obter o valor do field informado de um determinado objeto através do seu
	 * método get.
	 * 
	 * @see IllegalAccessException
	 * @see IllegalArgumentException
	 * @see InvocationTargetException
	 */
	public static Object getFieldValue(Object obj, Field field) {
		try {
			return getReadMethod(field).invoke(obj, (Object[]) null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Verificar se uma classe é associável a outra.
	 * 
	 */
	public static boolean isEqualsOrAssignableFrom(Class<?> base, Class<?> from) {
		return ((base.equals(from)) || (base.isAssignableFrom(from)));
	}

	/**
	 * Retorna o construtor ou nulo caso não exita
	 * 
	 * @see Class#getConstructors()
	 * @see Class#getDeclaredConstructor(Class...)
	 * @see NoSuchMethodException
	 * @see SecurityException
	 */
	public static Constructor<?> getConstructor(Class<?> objectClass, Class<?>... parameterTypes) {
		Constructor<?> constructor;
		try {
			constructor = objectClass.getConstructor(parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			constructor = null;
		}
		if (constructor == null) {
			try {
				constructor = objectClass.getDeclaredConstructor(parameterTypes);
			} catch (NoSuchMethodException | SecurityException e) {
				return null;
			}
		}
		if (constructor != null) {
			constructor.setAccessible(true);
		}
		return constructor;
	}

	/**
	 * Extende as funcionalidade do método contino em Class para trazer também
	 * classe dos tipos primitivos.
	 * 
	 * @param className Nome da classe.
	 * @return Classe aprtir do nome.
	 * @see Class#forName(java.lang.String)
	 * @see ClassNotFoundException
	 */
	public static Class<?> getClassForName(String className) {
		if ("byte".equals(className)) {
			return byte.class;
		} else if ("int".equals(className)) {
			return int.class;
		} else if ("long".equals(className)) {
			return long.class;
		} else if ("double".equals(className)) {
			return double.class;
		} else if ("float".equals(className)) {
			return float.class;
		} else if ("boolean".equals(className)) {
			return boolean.class;
		} else if ("char".equals(className)) {
			return char.class;
		} else if ("void".equals(className)) {
			return void.class;
		} else if ("short".equals(className)) {
			return short.class;
		} else
			try {
				return Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
	}
}