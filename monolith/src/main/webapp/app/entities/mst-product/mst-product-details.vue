<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="mstProduct">
        <h2 class="jh-entity-heading" data-cy="mstProductDetailsHeading">
          <span v-text="t$('monolithApp.mstProduct.detail.title')"></span> {{ mstProduct.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.mstProduct.name')"></span>
          </dt>
          <dd>
            <span>{{ mstProduct.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstProduct.description')"></span>
          </dt>
          <dd>
            <span>{{ mstProduct.description }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstProduct.price')"></span>
          </dt>
          <dd>
            <span>{{ mstProduct.price }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstProduct.quantity')"></span>
          </dt>
          <dd>
            <span>{{ mstProduct.quantity }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstProduct.barcode')"></span>
          </dt>
          <dd>
            <span>{{ mstProduct.barcode }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstProduct.unitSize')"></span>
          </dt>
          <dd>
            <span>{{ mstProduct.unitSize }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstProduct.category')"></span>
          </dt>
          <dd>
            <div v-if="mstProduct.category">
              <router-link :to="{ name: 'MstCategoryView', params: { mstCategoryId: mstProduct.category.id } }">{{
                mstProduct.category.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstProduct.brand')"></span>
          </dt>
          <dd>
            <div v-if="mstProduct.brand">
              <router-link :to="{ name: 'MstBrandView', params: { mstBrandId: mstProduct.brand.id } }">{{
                mstProduct.brand.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstProduct.mstSupplier')"></span>
          </dt>
          <dd>
            <div v-if="mstProduct.mstSupplier">
              <router-link :to="{ name: 'MstSupplierView', params: { mstSupplierId: mstProduct.mstSupplier.id } }">{{
                mstProduct.mstSupplier.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="mstProduct.id"
          :to="{ name: 'MstProductEdit', params: { mstProductId: mstProduct.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./mst-product-details.component.ts"></script>
