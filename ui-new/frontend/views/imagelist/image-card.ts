import '@vaadin/vaadin-ordered-layout/vaadin-horizontal-layout';
import '@vaadin/vaadin-ordered-layout/vaadin-vertical-layout';
import '@vaadin/vaadin-select';
import { html, LitElement } from 'lit';
import { customElement } from 'lit/decorators';

@customElement('image-card')
export class ImageCard extends LitElement {
  createRenderRoot() {
    // Do not use a shadow root
    return this;
  }

  render() {
    return html`<li class="bg-contrast-5 flex flex-col items-start p-m rounded-l">
      <div
        class="bg-contrast flex items-center justify-center mb-m overflow-hidden rounded-m w-full"
        style="height: 160px;"
      >
        <img id="image" class="w-full" />
      </div>
      <span class="text-xl font-semibold" id="header"></span>
      <span class="text-s text-secondary" id="subtitle"></span>
      <p class="my-m" id="text"></p>
      <span theme="badge" id="badge"></span>
    </li> `;
  }
}
