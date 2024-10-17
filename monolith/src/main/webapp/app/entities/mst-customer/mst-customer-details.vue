<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="mstCustomer">
        <h2 class="jh-entity-heading" data-cy="mstCustomerDetailsHeading">
          <span v-text="t$('monolithApp.mstCustomer.detail.title')"></span> {{ mstCustomer.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.mstCustomer.firstName')"></span>
          </dt>
          <dd>
            <span>{{ mstCustomer.firstName }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstCustomer.lastName')"></span>
          </dt>
          <dd>
            <span>{{ mstCustomer.lastName }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstCustomer.email')"></span>
          </dt>
          <dd>
            <span>{{ mstCustomer.email }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstCustomer.phoneNumber')"></span>
          </dt>
          <dd>
            <span>{{ mstCustomer.phoneNumber }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstCustomer.address')"></span>
          </dt>
          <dd>
            <span>{{ mstCustomer.address }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstCustomer.loyaltyPoints')"></span>
          </dt>
          <dd>
            <span>{{ mstCustomer.loyaltyPoints }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="mstCustomer.id"
          :to="{ name: 'MstCustomerEdit', params: { mstCustomerId: mstCustomer.id } }"
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

<script lang="ts" src="./mst-customer-details.component.ts"></script>
