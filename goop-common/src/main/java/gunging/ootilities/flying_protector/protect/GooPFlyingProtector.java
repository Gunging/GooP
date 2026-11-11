package gunging.ootilities.flying_protector.protect;

import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;
import java.util.Set;

/**
 *
 *
 * " These sentient statues guard this level from the unclean. That means you. "
 *
 *
 */
@SupportedAnnotationTypes({"gunging.ootilities.flying_protector.logic.GooPTrue", "gunging.ootilities.flying_protector.logic.GooPFalse"})
@SupportedSourceVersion(SourceVersion.RELEASE_16)
public class GooPFlyingProtector extends AbstractProcessor {

    @Override public boolean process(@NotNull Set<? extends TypeElement> annotations, @NotNull RoundEnvironment roundEnv) {

        // Good question
        TypeElement goopTrueTypeElement = processingEnv.getElementUtils().getTypeElement("gunging.ootilities.flying_protector.logic.GooPTrue");

        DeclaredType goopTrueType = processingEnv.getTypeUtils().getDeclaredType(goopTrueTypeElement);

        Set<? extends Element> goopTrueAnnotated = roundEnv.getElementsAnnotatedWith(goopTrueTypeElement);

        for (TypeElement type : ElementFilter.typesIn(goopTrueAnnotated)) {
            
        }

        return false;
    }
}
