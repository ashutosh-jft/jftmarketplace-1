import Ember from 'ember';

export default Ember.Controller.extend({
    
    store1: Ember.inject.service(),

    actions: {
        chooseAddressType(params) {
            console.info(params);
        },
        
        shipAddress(formData) {
            Ember.set(this.get('store1'), 'shipAddr', formData);
            this.transitionToRoute('summarypage');
        }
    }
});
