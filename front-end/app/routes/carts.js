import Ember from 'ember';

export default Ember.Route.extend({
    store1: Ember.inject.service(),

    model(){
        let post = {
            cartItems: Ember.get(this.get('store1'), 'cartItems')
        }
        return post;
    }
});
