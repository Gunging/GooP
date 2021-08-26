package gunging.ootilities.gunging_ootilities_plugin.misc;

public class RefSimulator<E> {
    E ref;
    public RefSimulator( E e ) { ref = e; }
    public E GetValue() { return ref; }
    public E getValue() { return ref; }
    public void SetValue( E e ){ this.ref = e; }
    public void setValue( E e ){ this.ref = e; }

    public String toString() { return ref.toString(); }
}