package askartius.mobctrlsys.api;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EspCommsViewModel extends ViewModel {
    private final MutableLiveData<EspComms> selectedEspComms = new MutableLiveData<>();

    public void selectEspComms(EspComms espComms) {
        selectedEspComms.setValue(espComms);
    }

    public LiveData<EspComms> getSelectedEspComms() {
        return selectedEspComms;
    }
}
