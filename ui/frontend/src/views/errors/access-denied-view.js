import { PolymerElement } from '@polymer/polymer/polymer-element.js';
import '../../../styles/shared-styles.js';
import { html } from '@polymer/polymer/lib/utils/html-tag.js';
class AccessDeniedView extends PolymerElement {
  static get template() {
    return html`
    <h3 style="text-align: center">
      Access denied
    </h3>
`;
  }

  static get is() {
    return 'access-denied-view';
  }
}
customElements.define(AccessDeniedView.is, AccessDeniedView);
