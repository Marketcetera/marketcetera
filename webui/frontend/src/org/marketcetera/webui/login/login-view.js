import {html, PolymerElement} from '@polymer/polymer/polymer-element.js';
import '@vaadin/vaadin-login/src/vaadin-login-form.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';

class LoginView extends PolymerElement {

    static get template() {
        return html`
<style include="shared-styles">
                :host {
                    display: block;
                    height: 100%;
                }
            </style>
<vaadin-horizontal-layout style="width: 100%; height: 100%; justify-content: center; align-items: center;">
 <vaadin-login-form id="vaadinLoginForm" style="flex-grow: 0;" no-forgot-password></vaadin-login-form>
</vaadin-horizontal-layout>
`;
    }

    static get is() {
        return 'login-view';
    }

    static get properties() {
        return {
            // Declare your properties here.
        };
    }
}

customElements.define(LoginView.is, LoginView);
