package com.seven.permissions.compiler;

import com.google.auto.service.AutoService;
import com.seven.annotations.NeedPermissions;
import com.seven.annotations.OnNeverAskPermissions;
import com.seven.annotations.OnPermissionsDenied;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;


import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Time:2019/12/5
 * <p>
 * Author:wangzhou
 * <p>
 * Description:
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({
        "com.seven.annotations.NeedPermissions",
        "com.seven.annotations.OnNeverAskPermissions",
        "com.seven.annotations.OnPermissionsDenied"

})
public class PermissionProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementUtils;
    private Filer filer;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        typeUtils = processingEnvironment.getTypeUtils();
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
    }

//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        Set<String> types = new LinkedHashSet<>();
//        types.add(NeedPermissions.class.getCanonicalName());
//        types.add(OnPermissionsDenied.class.getCanonicalName());
//        types.add(OnNeverAskPermissions.class.getCanonicalName());
//        return types;
//    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        messager.printMessage(Diagnostic.Kind.NOTE, "processing...");
        System.out.println("start process");
        Set<? extends Element> needPermissionsSet = roundEnvironment.getElementsAnnotatedWith(NeedPermissions.class);
        Map<String, List<ExecutableElement>> needPermissionMap = new HashMap<>();
        for (Element element : needPermissionsSet) {
            ExecutableElement executableElement = (ExecutableElement) element;
            String activityName = getActivityName(executableElement);  //获取类
            List<ExecutableElement> list = needPermissionMap.get(activityName);
            if (list == null) {
                list = new ArrayList<>();
                needPermissionMap.put(activityName, list);
            }
            list.add(executableElement);
            System.out.println("NeedPermission executableElement" + element.getSimpleName().toString());
        }

        Set<? extends Element> onNeverAskAgainSet = roundEnvironment.getElementsAnnotatedWith(OnNeverAskPermissions.class);
        Map<String, List<ExecutableElement>> onNeverAskAgainMap = new HashMap<>();
        for (Element element : onNeverAskAgainSet) {
            ExecutableElement executableElement = (ExecutableElement) element;
            String activityName = getActivityName(executableElement);
            List<ExecutableElement> list = onNeverAskAgainMap.get(activityName);
            if (list == null) {
                list = new ArrayList<>();
                onNeverAskAgainMap.put(activityName, list);
            }
            list.add(executableElement);
            System.out.println("NeedPermission executableElement" + element.getSimpleName().toString());
        }


        Set<? extends Element> onPermissionDeniedSet = roundEnvironment.getElementsAnnotatedWith(OnPermissionsDenied.class);
        Map<String, List<ExecutableElement>> onPermissionDeniedMap = new HashMap<>();
        for (Element element : onPermissionDeniedSet) {
            ExecutableElement executableElement = (ExecutableElement) element;
            String activityName = getActivityName(executableElement);
            List<ExecutableElement> list = onPermissionDeniedMap.get(activityName);
            if (list == null) {
                list = new ArrayList<>();
                onPermissionDeniedMap.put(activityName, list);
            }
            list.add(executableElement);
            System.out.println("NeedPermission executableElement" + element.getSimpleName().toString());
        }


        for (String activityName : needPermissionMap.keySet()) {


            List<ExecutableElement> needPermissionElements = needPermissionMap.get(activityName);
            List<ExecutableElement> onNeverAskAgainElements = onNeverAskAgainMap.get(activityName);
            List<ExecutableElement> onPermissionDeniedElements = onPermissionDeniedMap.get(activityName);

            final String CLASS_SUFFIX = "$Permissions";


//            Filer filer = processingEnv.getFiler();
            try {
                JavaFileObject javaFileObject = filer.createSourceFile(activityName + CLASS_SUFFIX);
                String packageName = getPackageName(needPermissionElements.get(0)); //获取注解地方的包名
                Writer writer = javaFileObject.openWriter();
                String activitySimpleName = needPermissionElements.get(0).getEnclosingElement()
                        .getSimpleName()
                        .toString() + CLASS_SUFFIX;


                writer.write("package " + packageName + ";\n");
                writer.write("import com.seven.permissions.library.listener.RequestPermission;\n");
                writer.write("import com.seven.permissions.library.utils.PermissionUtils;\n");
                writer.write("import android.support.v4.app.ActivityCompat;\n");
                writer.write("import java.lang.ref.WeakReference;\n");
                writer.write("import com.seven.permissions.library.listener.PermissionRequest;\n");
                writer.write("public class " + activitySimpleName + " implements RequestPermission<" + activityName + ">{\n");
                writer.write("private static final int REQUEST_CODE = 666;\n");
                writer.write("private static String[] PERMISSION;\n");
                writer.write("public void requestPermission(" + activityName + " target,String[] permissions) {\n");
                writer.write(" PERMISSION = permissions ;\n");
                writer.write(" if (PermissionUtils.hasSelfPermissions(target, permissions)) {\n");


                if (needPermissionElements != null) {
                    for (ExecutableElement executableElement : needPermissionElements) {
                        String methodName = executableElement.getSimpleName().toString();
                        writer.write("target." + methodName + "();\n");
                    }
                }

                writer.write("}else{  ActivityCompat.requestPermissions(target, PERMISSION, REQUEST_CODE);}}\n");


                writer.write("public void onRequestPermissionsResult(\n" + activityName + " target, int requestCode, int[] grantResults){");
                writer.write("     switch (requestCode) {\n" + "case REQUEST_CODE:\n" + "if (PermissionUtils.verifyPermissions(grantResults)) {\n");

                if (needPermissionElements != null) {
                    for (ExecutableElement executableElement : needPermissionElements) {
                        String methodName = executableElement.getSimpleName().toString();
                        writer.write("target." + methodName + "();\n");
                    }
                }
                writer.write("} else if (!PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION)) {\n");
                if (onNeverAskAgainElements != null) {
                    for (ExecutableElement executableElement : onNeverAskAgainElements) {
                        String methodName = executableElement.getSimpleName().toString();
                        writer.write("target." + methodName + "();\n");
                    }
                }
                writer.write("}else{\n");
                if (onPermissionDeniedElements != null) {
                    for (ExecutableElement executableElement : onPermissionDeniedElements) {
                        String methodName = executableElement.getSimpleName().toString();
                        writer.write("target." + methodName + "();\n");
                    }
                }
                writer.write(" } break;\n default:\nbreak;\n}\n }\n}");
                writer.flush();
                writer.close();

            } catch (Exception e) {
                e.printStackTrace();

            }


        }


        return false;
    }

    private String getPackageName(ExecutableElement executableElement) {
        TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
        String packageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
        return packageName;
    }

    private String getActivityName(ExecutableElement executableElement) {
        String packageName = getPackageName(executableElement);
        TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
        return packageName + "." + typeElement.getSimpleName().toString();
    }
}
