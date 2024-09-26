<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="trxTestimonial">
        <h2 class="jh-entity-heading" data-cy="trxTestimonialDetailsHeading">
          <span v-text="t$('monolithApp.trxTestimonial.detail.title')"></span> {{ trxTestimonial.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.trxTestimonial.name')"></span>
          </dt>
          <dd>
            <span>{{ trxTestimonial.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxTestimonial.feedback')"></span>
          </dt>
          <dd>
            <span>{{ trxTestimonial.feedback }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxTestimonial.rating')"></span>
          </dt>
          <dd>
            <span>{{ trxTestimonial.rating }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxTestimonial.date')"></span>
          </dt>
          <dd>
            <span v-if="trxTestimonial.date">{{ formatDateLong(trxTestimonial.date) }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="trxTestimonial.id"
          :to="{ name: 'TrxTestimonialEdit', params: { trxTestimonialId: trxTestimonial.id } }"
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

<script lang="ts" src="./trx-testimonial-details.component.ts"></script>
